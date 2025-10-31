// ----- Form & UI -----
const issueTypes = [
  'Road Damage',
  'Streetlight Outage',
  'Garbage Collection',
  'Water Leakage',
  'Illegal Parking',
  'Noise Complaint',
  'Other'
];

const formEl = document.getElementById('issueForm');
const typeEl = document.getElementById('issueType');
const titleEl = document.getElementById('title');
const descEl = document.getElementById('description');
const locEl = document.getElementById('locationInput');
const btnUseCurrent = document.getElementById('btnUseCurrent');
const resultEl = document.getElementById('submitResult');
const imageInput = document.getElementById('image');
const imagePreview = document.getElementById('imagePreview');
const imagePreviewImg = document.getElementById('imagePreviewImg');
const latEl = document.getElementById('lat');
const lngEl = document.getElementById('lng');
const mapFallback = document.getElementById('mapFallback');
const mapContainer = document.getElementById('map');

// entrance animation flag
document.addEventListener('DOMContentLoaded', () => {
  document.body.classList.add('ready');
});

// populate issue types
for (const t of issueTypes) {
  const opt = document.createElement('option');
  opt.value = t;
  opt.textContent = t;
  typeEl.appendChild(opt);
}

// Image preview
imageInput.addEventListener('change', () => {
  const file = imageInput.files && imageInput.files[0];
  if (!file) {
    imagePreview.hidden = true;
    return;
  }
  const url = URL.createObjectURL(file);
  imagePreviewImg.src = url;
  imagePreview.hidden = false;
});

// Button ripple effect for all .btn (disabled/loading safe)
document.addEventListener('click', (e) => {
  const btn = e.target.closest('.btn');
  if (!btn) return;
  if (btn.disabled || btn.classList.contains('loading')) return;
  const rect = btn.getBoundingClientRect();
  const ripple = document.createElement('span');
  ripple.className = 'ripple';
  ripple.style.left = `${e.clientX - rect.left}px`;
  ripple.style.top = `${e.clientY - rect.top}px`;
  btn.appendChild(ripple);
  setTimeout(() => ripple.remove(), 650);
});

// Validation helpers
function setError(id, message) {
  const el = document.getElementById(id);
  if (el) el.textContent = message || '';
}

function validateForm() {
  let valid = true;
  setError('issueTypeError');
  setError('titleError');
  setError('descriptionError');
  setError('locationError');

  if (!typeEl.value) { setError('issueTypeError', 'Please select an issue type.'); valid = false; }
  if (!titleEl.value.trim()) { setError('titleError', 'Title is required.'); valid = false; }
  if (!descEl.value.trim()) { setError('descriptionError', 'Description is required.'); valid = false; }
  if (!locEl.value.trim() || !latEl.value || !lngEl.value) {
    setError('locationError', 'Please choose a location on the map or use current location.');
    valid = false;
  }
  return valid;
}

formEl.addEventListener('submit', (e) => {
  e.preventDefault();
  if (!validateForm()) return;

  const payload = {
    type: typeEl.value,
    title: titleEl.value.trim(),
    description: descEl.value.trim(),
    locationText: locEl.value.trim(),
    lat: Number(latEl.value),
    lng: Number(lngEl.value),
    imageName: imageInput.files && imageInput.files[0] ? imageInput.files[0].name : null,
    createdAt: new Date().toISOString()
  };

  resultEl.hidden = false;
  resultEl.textContent = 'Submitted! Here is your report data (no backend wired):\n' + JSON.stringify(payload, null, 2);
  formEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
});

// ----- Google Maps Integration -----
let map, marker, autocomplete, geocoder;

function setLatLng(lat, lng, moveMap = true) {
  latEl.value = String(lat);
  lngEl.value = String(lng);
  if (marker) marker.setPosition({ lat, lng });
  if (moveMap && map) map.panTo({ lat, lng });
}

// Card tilt effect
(() => {
  const card = document.querySelector('.card');
  if (!card) return;
  const strength = 10; // degrees
  card.addEventListener('mousemove', (e) => {
    const rect = card.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    const rx = ((y / rect.height) - 0.5) * -strength;
    const ry = ((x / rect.width) - 0.5) * strength;
    card.style.transform = `perspective(900px) rotateX(${rx}deg) rotateY(${ry}deg)`;
  });
  card.addEventListener('mouseleave', () => {
    card.style.transform = 'perspective(900px) rotateX(0) rotateY(0)';
  });
})();

// Sidebar three-dot toggle for mobile
(() => {
  const toggle = document.getElementById('menuToggle');
  const overlay = document.getElementById('menuOverlay');
  const navLinks = document.querySelectorAll('.sidebar nav a');
  if (!toggle || !overlay) return;
  function openMenu() {
    document.body.classList.add('menu-open');
    overlay.hidden = false;
    toggle.setAttribute('aria-expanded', 'true');
  }
  function closeMenu() {
    document.body.classList.remove('menu-open');
    overlay.hidden = true;
    toggle.setAttribute('aria-expanded', 'false');
  }
  function toggleMenu() { document.body.classList.contains('menu-open') ? closeMenu() : openMenu(); }
  toggle.addEventListener('click', toggleMenu);
  overlay.addEventListener('click', closeMenu);
  document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeMenu(); });
  navLinks.forEach(a => a.addEventListener('click', closeMenu));
})();

function reverseGeocode(lat, lng) {
  if (!geocoder) return;
  geocoder.geocode({ location: { lat, lng } }, (results, status) => {
    if (status === 'OK' && results && results[0]) {
      locEl.value = results[0].formatted_address;
      setError('locationError');
    }
  });
}

function wireAutocomplete() {
  if (!window.google || !window.google.maps || !google.maps.places) return;
  autocomplete = new google.maps.places.Autocomplete(locEl, {
    fields: ['geometry', 'formatted_address', 'name']
  });
  autocomplete.addListener('place_changed', () => {
    const place = autocomplete.getPlace();
    if (!place || !place.geometry || !place.geometry.location) return;
    const pos = place.geometry.location;
    const lat = pos.lat();
    const lng = pos.lng();
    setLatLng(lat, lng);
    map.setZoom(16);
    setError('locationError');
  });
}

function enableMapClicks() {
  map.addListener('click', (e) => {
    const lat = e.latLng.lat();
    const lng = e.latLng.lng();
    setLatLng(lat, lng);
    reverseGeocode(lat, lng);
    setError('locationError');
  });
  marker.addListener('dragend', () => {
    const pos = marker.getPosition();
    const lat = pos.lat();
    const lng = pos.lng();
    setLatLng(lat, lng, false);
    reverseGeocode(lat, lng);
  });
}

function wireGeolocation() {
  btnUseCurrent.addEventListener('click', () => {
    if (!navigator.geolocation) {
      alert('Geolocation is not supported by your browser.');
      return;
    }
    btnUseCurrent.disabled = true;
    btnUseCurrent.classList.add('loading');

    let bestFix = null;
    const opts = { enableHighAccuracy: true, timeout: 20000, maximumAge: 0 };
    const targetAccuracyMeters = 40; // stop early when sufficiently accurate

    const finalize = () => {
      if (watchId != null) navigator.geolocation.clearWatch(watchId);
      btnUseCurrent.disabled = false;
      btnUseCurrent.classList.remove('loading');
      if (!bestFix) return;
      const { latitude: lat, longitude: lng } = bestFix.coords;
      setLatLng(lat, lng);
      map.setZoom(17);
      reverseGeocode(lat, lng);
      setError('locationError');
    };

    const watchId = navigator.geolocation.watchPosition(
      (pos) => {
        // Keep track of the best accuracy we have seen within the window
        if (!bestFix || pos.coords.accuracy < bestFix.coords.accuracy) {
          bestFix = pos;
          const lat = pos.coords.latitude;
          const lng = pos.coords.longitude;
          setLatLng(lat, lng); // live update so the user sees refinement
          if (map) map.setZoom(Math.max(map.getZoom() || 15, 16));
        }
        if (pos.coords.accuracy <= targetAccuracyMeters) {
          finalize();
        }
      },
      (err) => {
        console.warn('Geolocation error', err);
        alert('Could not fetch accurate location. Please try again outdoors or check permissions.');
        finalize();
      },
      opts
    );

    // Safety timeout: finalize with the best reading we have after 10s
    setTimeout(finalize, 10000);
  });
}

// initMap is called by Google Maps script when loaded
window.initMap = function initMap() {
  if (!window.google || !google.maps) {
    mapFallback.hidden = false;
    return;
  }

  geocoder = new google.maps.Geocoder();
  const initial = { lat: 28.6139, lng: 77.2090 }; // New Delhi as a neutral default
  map = new google.maps.Map(document.getElementById('map'), {
    center: initial,
    zoom: 12,
    mapTypeControl: false,
    streetViewControl: false,
    fullscreenControl: true
  });
  marker = new google.maps.Marker({ position: initial, map, draggable: true });
  setLatLng(initial.lat, initial.lng, false);

  wireAutocomplete();
  enableMapClicks();
  wireGeolocation();

  // remove loading shimmer
  if (mapContainer) mapContainer.classList.remove('loading');
};

// If the Google script fails to load, show fallback after a tick
window.addEventListener('error', () => {
  if (window.__gmapsFailed) mapFallback.hidden = false;
});


