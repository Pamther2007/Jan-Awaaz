Civic Issue Reporter (Front-end)
================================

A simple, single-page web app to report civic issues with a Google Map location picker.

Run locally
----------
Open `index.html` in your browser. No build steps needed.

Google Maps API key
-------------------
This page uses the Google Maps JavaScript API with Places Autocomplete.

1. Get an API key from Google Cloud Console.
2. Enable: Maps JavaScript API, Places API, Geocoding API.
3. In `index.html`, replace `YOUR_API_KEY` in the script URL with your key.

Features
--------
- Issue category select, title, and description with validation
- Location via Places Autocomplete, map click/drag, or Use Current Location
- Image upload with preview
- Client-side submission prints JSON summary (no backend)

Notes
-----
- Default map center is New Delhi. Update in `app.js` if desired.
- Without a valid key, the map shows a fallback message.


