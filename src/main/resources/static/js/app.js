/**
 * RailNova - Advanced Railway Ticket Booking Web Application
 * Client Application Logic & REST API Integrations
 */

// Application State
const RailNova = {
    apiBase: '',
    token: localStorage.getItem('rn_token') || null,
    user: JSON.parse(localStorage.getItem('rn_user') || 'null'),
    stations: [],
    searchResults: [],
    activeTrain: null,
    selectedClass: null,
    selectedCoach: null,
    selectedSeats: [], // Array of seat objects { id, seatNumber, berthType, coachCode }
    currentBooking: null,
    adminStats: null,
    charts: {
        bookingsChart: null,
        trainTypeChart: null
    }
};

// ============================================================================
// Initialization
// ============================================================================
document.addEventListener('DOMContentLoaded', () => {
    initApp();
});

async function initApp() {
    setupEventListeners();
    updateAuthUI();
    await loadStations();

    // Set default journey date to tomorrow
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const dateInput = document.getElementById('searchJourneyDate');
    if (dateInput) {
        dateInput.value = tomorrow.toISOString().split('T')[0];
        dateInput.min = new Date().toISOString().split('T')[0];
    }

    // Default search on homepage to showcase trains immediately
    executeTrainSearch();
}

function setupEventListeners() {
    // Search Form
    const searchForm = document.getElementById('trainSearchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            executeTrainSearch();
        });
    }

    // Station Swap Button
    const swapBtn = document.getElementById('swapStationsBtn');
    if (swapBtn) {
        swapBtn.addEventListener('click', swapStations);
    }

    // Auth Forms
    const loginForm = document.getElementById('loginForm');
    if (loginForm) loginForm.addEventListener('submit', handleLogin);

    const registerForm = document.getElementById('registerForm');
    if (registerForm) registerForm.addEventListener('submit', handleRegister);

    const forgotForm = document.getElementById('forgotPasswordForm');
    if (forgotForm) forgotForm.addEventListener('submit', handleForgotPassword);

    // PNR Form
    const pnrForm = document.getElementById('pnrSearchForm');
    if (pnrForm) pnrForm.addEventListener('submit', handlePnrSearch);

    // Filters and Sorting
    document.querySelectorAll('.train-filter').forEach(el => {
        el.addEventListener('change', applyClientFilters);
    });

    const priceRange = document.getElementById('priceRange');
    if (priceRange) {
        priceRange.addEventListener('input', (e) => {
            document.getElementById('priceValue').textContent = `₹${e.target.value}`;
            applyClientFilters();
        });
    }

    document.querySelectorAll('.sort-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('.sort-btn').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');
            applyClientSorting(e.target.dataset.sort);
        });
    });

    // Profile Form
    const profileForm = document.getElementById('profileForm');
    if (profileForm) profileForm.addEventListener('submit', handleProfileUpdate);

    // Support Ticket Form
    const supportForm = document.getElementById('supportTicketForm');
    if (supportForm) supportForm.addEventListener('submit', handleSupportSubmit);

    // Add Train Form (Admin)
    const addTrainForm = document.getElementById('addTrainForm');
    if (addTrainForm) addTrainForm.addEventListener('submit', handleAddTrain);
}

// ============================================================================
// API Helper
// ============================================================================
async function fetchApi(endpoint, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };

    if (RailNova.token) {
        headers['Authorization'] = `Bearer ${RailNova.token}`;
    }

    try {
        const response = await fetch(`${RailNova.apiBase}${endpoint}`, {
            ...options,
            headers
        });

        const data = await response.json().catch(() => null);

        if (!response.ok) {
            const errorMsg = data && data.message ? data.message : `Request failed with status ${response.status}`;
            throw new Error(errorMsg);
        }

        return data;
    } catch (err) {
        console.error(`API Error on ${endpoint}:`, err);
        throw err;
    }
}

// ============================================================================
// Authentication & User State
// ============================================================================
function updateAuthUI() {
    const guestNav = document.getElementById('navGuestSection');
    const userNav = document.getElementById('navUserSection');
    const adminLink = document.getElementById('navAdminLink');
    const staffLink = document.getElementById('navStaffLink');

    if (RailNova.token && RailNova.user) {
        if (guestNav) guestNav.classList.add('d-none');
        if (userNav) userNav.classList.remove('d-none');

        const userNameEl = document.getElementById('navUserName');
        if (userNameEl) userNameEl.textContent = RailNova.user.fullName || RailNova.user.email;

        const roleBadge = document.getElementById('navRoleBadge');
        if (roleBadge) {
            roleBadge.textContent = RailNova.user.role.replace('ROLE_', '');
            roleBadge.className = 'badge ms-1 ' + (
                RailNova.user.role === 'ROLE_ADMIN' ? 'badge-role-admin' :
                RailNova.user.role === 'ROLE_STAFF' ? 'badge-role-staff' : 'badge-role-passenger'
            );
        }

        if (adminLink) {
            if (RailNova.user.role === 'ROLE_ADMIN') adminLink.classList.remove('d-none');
            else adminLink.classList.add('d-none');
        }

        if (staffLink) {
            if (RailNova.user.role === 'ROLE_STAFF' || RailNova.user.role === 'ROLE_ADMIN') {
                staffLink.classList.remove('d-none');
            } else {
                staffLink.classList.add('d-none');
            }
        }
    } else {
        if (guestNav) guestNav.classList.remove('d-none');
        if (userNav) userNav.classList.add('d-none');
        if (adminLink) adminLink.classList.add('d-none');
        if (staffLink) staffLink.classList.add('d-none');
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;
    const btn = document.getElementById('loginSubmitBtn');

    try {
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Logging in...';

        const res = await fetchApi('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });

        RailNova.token = res.data.token;
        RailNova.user = res.data.user;
        localStorage.setItem('rn_token', RailNova.token);
        localStorage.setItem('rn_user', JSON.stringify(RailNova.user));

        bootstrap.Modal.getInstance(document.getElementById('authModal')).hide();
        updateAuthUI();
        showAlert('Logged in successfully! Welcome back, ' + RailNova.user.fullName, 'success');

        if (RailNova.user.role === 'ROLE_ADMIN') {
            showSection('adminSection');
            loadAdminDashboard();
        }
    } catch (err) {
        showAlert(err.message || 'Login failed. Please check your credentials.', 'danger', 'authAlertBox');
    } finally {
        btn.disabled = false;
        btn.innerHTML = 'Log In';
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const phone = document.getElementById('regPhone').value.trim();
    const password = document.getElementById('regPassword').value;
    const btn = document.getElementById('regSubmitBtn');

    try {
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Creating account...';

        const res = await fetchApi('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify({ fullName, email, phone, password })
        });

        RailNova.token = res.data.token;
        RailNova.user = res.data.user;
        localStorage.setItem('rn_token', RailNova.token);
        localStorage.setItem('rn_user', JSON.stringify(RailNova.user));

        bootstrap.Modal.getInstance(document.getElementById('authModal')).hide();
        updateAuthUI();
        showAlert('Account created successfully! Welcome to RailNova.', 'success');
    } catch (err) {
        showAlert(err.message || 'Registration failed.', 'danger', 'authAlertBox');
    } finally {
        btn.disabled = false;
        btn.innerHTML = 'Create Account';
    }
}

async function handleForgotPassword(e) {
    e.preventDefault();
    const email = document.getElementById('forgotEmail').value.trim();
    const newPassword = document.getElementById('forgotNewPassword').value;
    const btn = document.getElementById('forgotSubmitBtn');

    try {
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Updating...';

        await fetchApi('/api/auth/forgot-password', {
            method: 'POST',
            body: JSON.stringify({ email, newPassword: newPassword || 'Password@123' })
        });

        showAlert('Password reset instructions processed! You can now log in with the new password.', 'success', 'authAlertBox');
        document.getElementById('tabLoginBtn').click();
    } catch (err) {
        showAlert(err.message || 'Failed to process password reset.', 'danger', 'authAlertBox');
    } finally {
        btn.disabled = false;
        btn.innerHTML = 'Reset Password';
    }
}

function quickDemoLogin(role) {
    let email, pass;
    if (role === 'admin') {
        email = 'admin@railnova.com';
        pass = 'Admin@123';
    } else if (role === 'staff') {
        email = 'staff@railnova.com';
        pass = 'Staff@123';
    } else {
        email = 'user@railnova.com';
        pass = 'User@123';
    }

    document.getElementById('loginEmail').value = email;
    document.getElementById('loginPassword').value = pass;
    document.getElementById('loginForm').dispatchEvent(new Event('submit'));
}

function handleLogout() {
    RailNova.token = null;
    RailNova.user = null;
    localStorage.removeItem('rn_token');
    localStorage.removeItem('rn_user');
    updateAuthUI();
    showSection('homeSection');
    showAlert('You have been logged out safely.', 'info');
}

// ============================================================================
// Stations & Autocomplete
// ============================================================================
async function loadStations() {
    try {
        const res = await fetchApi('/api/stations');
        RailNova.stations = res.data || [];
        populateStationDataLists();
    } catch (err) {
        console.error('Failed to load stations', err);
    }
}

function populateStationDataLists() {
    const datalistFrom = document.getElementById('stationsListFrom');
    const datalistTo = document.getElementById('stationsListTo');

    if (!datalistFrom || !datalistTo) return;

    datalistFrom.innerHTML = '';
    datalistTo.innerHTML = '';

    RailNova.stations.forEach(st => {
        const opt = document.createElement('option');
        opt.value = `${st.name} (${st.code})`;
        opt.dataset.code = st.code;
        opt.dataset.id = st.id;

        datalistFrom.appendChild(opt.cloneNode(true));
        datalistTo.appendChild(opt);
    });

    // Populate Admin Station Selects if present
    const sourceSelect = document.getElementById('adminTrainSource');
    const destSelect = document.getElementById('adminTrainDest');
    if (sourceSelect && destSelect) {
        sourceSelect.innerHTML = '<option value="">Select Origin Station</option>';
        destSelect.innerHTML = '<option value="">Select Destination Station</option>';
        RailNova.stations.forEach(st => {
            const opt = `<option value="${st.id}">${st.name} (${st.code}) - ${st.city}</option>`;
            sourceSelect.insertAdjacentHTML('beforeend', opt);
            destSelect.insertAdjacentHTML('beforeend', opt);
        });
    }
}

function swapStations() {
    const fromInput = document.getElementById('searchFromStation');
    const toInput = document.getElementById('searchToStation');
    if (fromInput && toInput) {
        const temp = fromInput.value;
        fromInput.value = toInput.value;
        toInput.value = temp;
    }
}

function extractStationCode(value) {
    if (!value) return '';
    const match = value.match(/\(([^)]+)\)/);
    if (match) return match[1].trim();
    return value.trim();
}

// ============================================================================
// Train Search & Filtering
// ============================================================================
async function executeTrainSearch(fromOverride, toOverride) {
    const fromVal = fromOverride || document.getElementById('searchFromStation')?.value;
    const toVal = toOverride || document.getElementById('searchToStation')?.value;
    const dateVal = document.getElementById('searchJourneyDate')?.value;
    const classVal = document.getElementById('searchClassFilter')?.value;

    const fromCode = extractStationCode(fromVal);
    const toCode = extractStationCode(toVal);

    const queryParams = new URLSearchParams();
    if (fromCode) queryParams.append('from', fromCode);
    if (toCode) queryParams.append('to', toCode);
    if (dateVal) queryParams.append('date', dateVal);
    if (classVal && classVal !== 'ALL') queryParams.append('classType', classVal);

    showResultsLoading();

    try {
        const res = await fetchApi(`/api/trains/search?${queryParams.toString()}`);
        RailNova.searchResults = res.data || [];
        applyClientFilters();

        const countEl = document.getElementById('searchResultsCount');
        if (countEl) countEl.textContent = `${RailNova.searchResults.length} Trains Found`;

        // Smooth scroll to search results section if searching with inputs
        if (fromOverride || fromVal) {
            document.getElementById('searchResultsSection')?.scrollIntoView({ behavior: 'smooth' });
        }
    } catch (err) {
        showResultsError(err.message || 'Error searching trains');
    }
}

function applyClientFilters() {
    const container = document.getElementById('trainCardsContainer');
    if (!container) return;

    // Check selected train types
    const selectedTypes = Array.from(document.querySelectorAll('.filter-train-type:checked')).map(el => el.value);
    const selectedClasses = Array.from(document.querySelectorAll('.filter-class:checked')).map(el => el.value);
    const maxPrice = parseFloat(document.getElementById('priceRange')?.value || '5000');
    const selectedTime = document.querySelector('input[name="filterTime"]:checked')?.value || 'ALL';

    const filtered = RailNova.searchResults.filter(t => {
        // Train Type filter
        if (selectedTypes.length > 0 && !selectedTypes.includes(t.trainType)) return false;

        // Price filter
        if (t.startingFare > maxPrice) return false;

        // Class filter
        if (selectedClasses.length > 0) {
            const hasClass = t.classAvailabilities.some(ca =>
                selectedClasses.includes(ca.coachTypeCode) || selectedClasses.includes(ca.coachType)
            );
            if (!hasClass) return false;
        }

        // Time slot filter
        if (selectedTime !== 'ALL') {
            const hour = parseInt(t.departureTime.split(':')[0]);
            if (selectedTime === 'early' && hour >= 6) return false;
            if (selectedTime === 'morning' && (hour < 6 || hour >= 12)) return false;
            if (selectedTime === 'afternoon' && (hour < 12 || hour >= 18)) return false;
            if (selectedTime === 'evening' && (hour < 18 || hour >= 24)) return false;
        }

        return true;
    });

    renderTrainCards(filtered);
}

function applyClientSorting(sortType) {
    if (sortType === 'lowest_fare') {
        RailNova.searchResults.sort((a, b) => a.startingFare - b.startingFare);
    } else if (sortType === 'earliest_departure') {
        RailNova.searchResults.sort((a, b) => a.departureTime.localeCompare(b.departureTime));
    } else if (sortType === 'shortest_duration') {
        RailNova.searchResults.sort((a, b) => a.durationMinutes - b.durationMinutes);
    }
    applyClientFilters();
}

function renderTrainCards(trains) {
    const container = document.getElementById('trainCardsContainer');
    if (!container) return;

    if (trains.length === 0) {
        container.innerHTML = `
            <div class="card p-5 text-center border-0 shadow-sm rounded-4">
                <i class="bi bi-train-front text-muted" style="font-size: 3.5rem;"></i>
                <h4 class="fw-bold mt-3">No Trains Found</h4>
                <p class="text-muted">No trains match the selected filters or running days. Try adjusting dates or filters.</p>
                <div class="mt-2">
                    <button class="btn btn-outline-primary px-4 rounded-pill" onclick="resetFilters()">Reset All Filters</button>
                </div>
            </div>
        `;
        return;
    }

    let html = '';
    trains.forEach(t => {
        let badgeClass = 'rn-badge-superfast';
        if (t.trainType === 'VANDE_BHARAT') badgeClass = 'rn-badge-vande-bharat';
        else if (t.trainType === 'RAJDHANI') badgeClass = 'rn-badge-rajdhani';
        else if (t.trainType === 'SHATABDI') badgeClass = 'rn-badge-shatabdi';

        // Class availability blocks
        let classesHtml = '';
        t.classAvailabilities.forEach(ca => {
            const isAvailable = ca.availableSeats > 5;
            const isRac = ca.availableSeats > 0 && ca.availableSeats <= 5;
            const statusClass = isAvailable ? 'text-success' : (isRac ? 'text-warning' : 'text-danger');

            classesHtml += `
                <div class="rn-class-box" onclick="selectClassForBooking(${t.id}, '${ca.coachTypeCode}', ${ca.fare})">
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="rn-class-code">${ca.coachTypeCode}</span>
                        <span class="rn-class-fare">₹${ca.fare}</span>
                    </div>
                    <div class="rn-class-avail ${statusClass} mt-1">
                        <i class="bi bi-${isAvailable ? 'check-circle-fill' : 'exclamation-circle-fill'} me-1"></i>
                        ${ca.status}
                    </div>
                </div>
            `;
        });

        html += `
            <div class="rn-train-card fade-in">
                <div class="d-flex flex-wrap justify-content-between align-items-center mb-2">
                    <div class="d-flex align-items-center gap-2">
                        <span class="rn-train-badge ${badgeClass}">${t.trainTypeName}</span>
                        <h5 class="fw-bold mb-0 text-dark">${t.trainName}</h5>
                        <span class="badge bg-light text-dark border">#${t.trainNumber}</span>
                    </div>
                    <div class="text-muted small">
                        Runs On: <strong class="text-dark">${t.runsOnDays}</strong>
                    </div>
                </div>

                <div class="rn-timeline-track">
                    <div>
                        <div class="rn-time-text">${t.departureTime}</div>
                        <div class="rn-station-code">${t.sourceStationName} (${t.sourceStationCode})</div>
                    </div>
                    <div class="rn-duration-bar">
                        <span class="badge bg-light text-muted border px-2 py-1 small">${t.durationFormatted}</span>
                        <div class="rn-duration-line"></div>
                        <span class="small text-muted">Direct / Express</span>
                    </div>
                    <div class="text-end">
                        <div class="rn-time-text">${t.arrivalTime}</div>
                        <div class="rn-station-code">${t.destinationStationName} (${t.destinationStationCode})</div>
                    </div>
                </div>

                <div class="mt-3">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <span class="small text-muted fw-bold text-uppercase">Select Class & Availability</span>
                        <button class="btn btn-link btn-sm text-decoration-none p-0" onclick="showTrainRouteModal(${t.id})">
                            <i class="bi bi-geo-alt me-1"></i>View Route & Timetable
                        </button>
                    </div>
                    <div class="rn-class-grid">
                        ${classesHtml}
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
                    <div>
                        <span class="small text-muted">Starting from </span>
                        <span class="fs-5 fw-bold text-primary">₹${t.startingFare}</span>
                    </div>
                    <div class="d-flex gap-2">
                        <button class="btn btn-outline-secondary btn-sm px-3" onclick="showTrainRouteModal(${t.id})">
                            Route Details
                        </button>
                        <button class="btn rn-btn-primary btn-sm px-4" onclick="initiateBooking(${t.id})">
                            Book Now <i class="bi bi-arrow-right ms-1"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
    });

    container.innerHTML = html;
}

function showResultsLoading() {
    const container = document.getElementById('trainCardsContainer');
    if (container) {
        container.innerHTML = `
            <div class="p-5 text-center">
                <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;" role="status"></div>
                <h5 class="fw-bold mt-3">Fetching Live Train Schedules...</h5>
                <p class="text-muted small">Checking real-time seat availability across all coaches</p>
            </div>
        `;
    }
}

function showResultsError(message) {
    const container = document.getElementById('trainCardsContainer');
    if (container) {
        container.innerHTML = `
            <div class="alert alert-danger p-4 text-center rounded-4">
                <i class="bi bi-exclamation-triangle-fill fs-2"></i>
                <h5 class="fw-bold mt-2">Search Error</h5>
                <p class="mb-0">${message}</p>
            </div>
        `;
    }
}

function resetFilters() {
    document.querySelectorAll('.filter-train-type, .filter-class').forEach(el => el.checked = false);
    const defaultTime = document.getElementById('timeAll');
    if (defaultTime) defaultTime.checked = true;
    const priceRange = document.getElementById('priceRange');
    if (priceRange) {
        priceRange.value = 5000;
        document.getElementById('priceValue').textContent = '₹5000';
    }
    applyClientFilters();
}

function quickSearchRoute(from, to) {
    const fromInput = document.getElementById('searchFromStation');
    const toInput = document.getElementById('searchToStation');
    if (fromInput && toInput) {
        fromInput.value = from;
        toInput.value = to;
        executeTrainSearch(from, to);
    }
}

// ============================================================================
// Train Route & Schedule Modal
// ============================================================================
async function showTrainRouteModal(trainId) {
    const modal = new bootstrap.Modal(document.getElementById('trainRouteModal'));
    modal.show();

    const titleEl = document.getElementById('routeModalTitle');
    const bodyEl = document.getElementById('routeModalBody');
    bodyEl.innerHTML = '<div class="text-center p-4"><div class="spinner-border text-primary"></div></div>';

    try {
        const date = document.getElementById('searchJourneyDate')?.value;
        const res = await fetchApi(`/api/trains/${trainId}/details?date=${date || ''}`);
        const train = res.data;

        titleEl.textContent = `${train.trainNumber} - ${train.trainName} (Route & Schedule)`;

        let stopsHtml = '';
        train.routeStops.forEach(stop => {
            stopsHtml += `
                <tr>
                    <td class="fw-bold">${stop.stopNumber}</td>
                    <td>
                        <div class="fw-bold">${stop.stationName}</div>
                        <span class="small text-muted font-monospace">${stop.stationCode}</span>
                    </td>
                    <td>${stop.arrivalTime}</td>
                    <td>${stop.departureTime}</td>
                    <td>${stop.haltMinutes > 0 ? stop.haltMinutes + ' mins' : '-'}</td>
                    <td>${stop.distanceKm} km</td>
                    <td><span class="badge bg-secondary">PF ${stop.platform}</span></td>
                </tr>
            `;
        });

        bodyEl.innerHTML = `
            <div class="p-3 bg-light rounded-3 mb-3 d-flex justify-content-between align-items-center">
                <div>
                    <span class="badge bg-primary me-2">${train.trainTypeName}</span>
                    <span class="text-muted">Origin:</span> <strong>${train.sourceStationName}</strong> 
                    <i class="bi bi-arrow-right mx-1"></i> 
                    <span class="text-muted">Destination:</span> <strong>${train.destinationStationName}</strong>
                </div>
                <div class="small text-muted">
                    Runs: <strong>${train.runsOnDays}</strong>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>#</th>
                            <th>Station</th>
                            <th>Arr.</th>
                            <th>Dep.</th>
                            <th>Halt</th>
                            <th>Distance</th>
                            <th>Platform</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${stopsHtml}
                    </tbody>
                </table>
            </div>
        `;
    } catch (err) {
        bodyEl.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
    }
}

// ============================================================================
// Booking Workflow
// ============================================================================
function selectClassForBooking(trainId, classCode, fare) {
    RailNova.selectedClass = classCode;
    initiateBooking(trainId, classCode);
}

async function initiateBooking(trainId, preselectedClass) {
    if (!RailNova.token) {
        showAlert('Please log in or create an account to proceed with ticket booking.', 'warning');
        const authModal = new bootstrap.Modal(document.getElementById('authModal'));
        authModal.show();
        return;
    }

    try {
        const date = document.getElementById('searchJourneyDate')?.value;
        const res = await fetchApi(`/api/trains/${trainId}/details?date=${date || ''}`);
        RailNova.activeTrain = res.data;

        // Open Booking Modal
        const bookingModal = new bootstrap.Modal(document.getElementById('bookingModal'));
        bookingModal.show();

        renderBookingModalHeader();
        renderClassOptions(preselectedClass);
        setupDefaultPassenger();
        recalculateFare();
    } catch (err) {
        showAlert(err.message || 'Error initiating booking', 'danger');
    }
}

function renderBookingModalHeader() {
    const headerEl = document.getElementById('bookingModalTrainInfo');
    if (!headerEl || !RailNova.activeTrain) return;

    const t = RailNova.activeTrain;
    const date = document.getElementById('searchJourneyDate')?.value || 'Upcoming';

    headerEl.innerHTML = `
        <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
            <div>
                <h5 class="fw-bold mb-1">${t.trainName} <span class="badge bg-primary">#${t.trainNumber}</span></h5>
                <div class="text-muted small">
                    <strong>${t.sourceStationName}</strong> (${t.departureTime}) 
                    <i class="bi bi-arrow-right mx-1"></i> 
                    <strong>${t.destinationStationName}</strong> (${t.arrivalTime})
                </div>
            </div>
            <div class="text-end">
                <div class="badge bg-light text-dark border px-3 py-2 fs-6">
                    <i class="bi bi-calendar3 me-1"></i> ${formatDate(date)}
                </div>
            </div>
        </div>
    `;
}

function renderClassOptions(preselectedClass) {
    const selectEl = document.getElementById('bookingClassSelect');
    if (!selectEl || !RailNova.activeTrain) return;

    selectEl.innerHTML = '';
    RailNova.activeTrain.classes.forEach(c => {
        const opt = document.createElement('option');
        opt.value = c.coachTypeCode;
        opt.dataset.fare = c.fare;
        opt.textContent = `${c.coachTypeCode} - ${c.coachTypeName} (₹${c.fare}) [${c.status}]`;

        if (preselectedClass && (c.coachTypeCode === preselectedClass || c.coachType === preselectedClass)) {
            opt.selected = true;
            RailNova.selectedClass = c.coachTypeCode;
        }
        selectEl.appendChild(opt);
    });

    if (!preselectedClass && RailNova.activeTrain.classes.length > 0) {
        RailNova.selectedClass = RailNova.activeTrain.classes[0].coachTypeCode;
    }

    selectEl.addEventListener('change', (e) => {
        RailNova.selectedClass = e.target.value;
        recalculateFare();
    });
}

function setupDefaultPassenger() {
    const container = document.getElementById('passengersContainer');
    if (!container) return;

    container.innerHTML = '';
    RailNova.selectedSeats = [];
    addPassengerRow(RailNova.user?.fullName || '', 28, 'MALE');
}

function addPassengerRow(defaultName = '', defaultAge = 25, defaultGender = 'MALE') {
    const container = document.getElementById('passengersContainer');
    if (!container) return;

    const count = container.querySelectorAll('.passenger-row').length + 1;
    if (count > 6) {
        showAlert('Maximum 6 passengers allowed per booking as per railway regulations.', 'warning');
        return;
    }

    const rowId = `passenger_${Date.now()}_${count}`;
    const rowHtml = `
        <div class="passenger-row card p-3 mb-3 border-light bg-light rounded-3" id="${rowId}">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h6 class="fw-bold mb-0 text-primary"><i class="bi bi-person-fill me-1"></i>Passenger #${count}</h6>
                ${count > 1 ? `<button type="button" class="btn btn-outline-danger btn-sm p-1 px-2" onclick="removePassengerRow('${rowId}')"><i class="bi bi-trash"></i> Remove</button>` : ''}
            </div>
            <div class="row g-2">
                <div class="col-md-5">
                    <label class="form-label small fw-semibold">Full Name</label>
                    <input type="text" class="form-control form-control-sm pass-name" value="${defaultName}" placeholder="As per official ID" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label small fw-semibold">Age</label>
                    <input type="number" class="form-control form-control-sm pass-age" value="${defaultAge}" min="1" max="120" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-semibold">Gender</label>
                    <select class="form-select form-select-sm pass-gender">
                        <option value="MALE" ${defaultGender === 'MALE' ? 'selected' : ''}>Male</option>
                        <option value="FEMALE" ${defaultGender === 'FEMALE' ? 'selected' : ''}>Female</option>
                        <option value="OTHER">Other</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small fw-semibold">Berth</label>
                    <select class="form-select form-select-sm pass-berth">
                        <option value="NO_PREF">No Pref</option>
                        <option value="LOWER">Lower</option>
                        <option value="MIDDLE">Middle</option>
                        <option value="UPPER">Upper</option>
                        <option value="SIDE_LOWER">Side Lower</option>
                        <option value="SIDE_UPPER">Side Upper</option>
                    </select>
                </div>
            </div>
        </div>
    `;

    container.insertAdjacentHTML('beforeend', rowHtml);
    recalculateFare();
}

function removePassengerRow(rowId) {
    const el = document.getElementById(rowId);
    if (el) el.remove();
    recalculateFare();
}

function recalculateFare() {
    const passengerRows = document.querySelectorAll('.passenger-row');
    const passengerCount = Math.max(1, passengerRows.length);

    const classSelect = document.getElementById('bookingClassSelect');
    const selectedOption = classSelect?.options[classSelect.selectedIndex];
    const baseFarePerPassenger = selectedOption ? parseFloat(selectedOption.dataset.fare || 0) : 1000.0;

    const reservationFee = 40.0 * passengerCount;
    const superfastFee = 45.0 * passengerCount;
    const subtotal = (baseFarePerPassenger * passengerCount) + reservationFee + superfastFee;
    const gstAmount = Math.round((subtotal * 0.05) * 100) / 100;

    // Check Coupon Discount
    const couponInput = document.getElementById('couponCodeInput')?.value.trim().toUpperCase();
    let discount = 0;
    if (couponInput === 'RAILNOVA50') {
        discount = 50.0;
        document.getElementById('couponSuccessMsg')?.classList.remove('d-none');
    } else {
        document.getElementById('couponSuccessMsg')?.classList.add('d-none');
    }

    const totalAmount = Math.max(0, Math.round((subtotal + gstAmount - discount) * 100) / 100);

    // Update UI elements
    const passCountEl = document.getElementById('fareSummaryPassCount');
    if (passCountEl) passCountEl.textContent = passengerCount;

    const baseEl = document.getElementById('fareSummaryBase');
    if (baseEl) baseEl.textContent = `₹${baseFarePerPassenger * passengerCount}`;

    const feeEl = document.getElementById('fareSummaryFees');
    if (feeEl) feeEl.textContent = `₹${reservationFee + superfastFee}`;

    const gstEl = document.getElementById('fareSummaryGst');
    if (gstEl) gstEl.textContent = `₹${gstAmount}`;

    const discountEl = document.getElementById('fareSummaryDiscount');
    if (discountEl) discountEl.textContent = `-₹${discount}`;

    const totalEl = document.getElementById('fareSummaryTotal');
    if (totalEl) totalEl.textContent = `₹${totalAmount}`;
}

// ============================================================================
// Interactive Visual Coach & Seat Layout
// ============================================================================
async function openSeatPickerModal() {
    if (!RailNova.activeTrain) return;

    const modal = new bootstrap.Modal(document.getElementById('seatPickerModal'));
    modal.show();

    // Find coaches matching currently selected class
    const selectedClass = RailNova.selectedClass || '3A';
    const eligibleCoaches = RailNova.activeTrain.coaches.filter(c =>
        c.coachTypeCode === selectedClass || c.coachType === selectedClass
    );

    const coachSelect = document.getElementById('seatCoachSelector');
    coachSelect.innerHTML = '';

    eligibleCoaches.forEach((c, idx) => {
        const opt = document.createElement('option');
        opt.value = c.id;
        opt.textContent = `Coach ${c.coachCode} (${c.totalSeats} seats)`;
        if (idx === 0) opt.selected = true;
        coachSelect.appendChild(opt);
    });

    coachSelect.onchange = () => loadCoachSeats(coachSelect.value);

    if (eligibleCoaches.length > 0) {
        await loadCoachSeats(eligibleCoaches[0].id);
    } else {
        document.getElementById('coachSeatGrid').innerHTML = '<div class="alert alert-info">General coach layout automatically allocated upon booking.</div>';
    }
}

async function loadCoachSeats(coachId) {
    const grid = document.getElementById('coachSeatGrid');
    grid.innerHTML = '<div class="text-center p-4"><div class="spinner-border text-primary"></div></div>';

    try {
        const date = document.getElementById('searchJourneyDate')?.value;
        const res = await fetchApi(`/api/trains/coaches/${coachId}/seats?date=${date || ''}`);
        const data = res.data;

        RailNova.selectedCoach = data;

        let seatsHtml = '';
        data.seats.forEach((s, idx) => {
            const isBooked = s.status === 'BOOKED';
            const isSelected = RailNova.selectedSeats.some(sel => sel.id === s.id);
            const statusClass = isBooked ? 'booked' : (isSelected ? 'selected' : '');

            // Add aisle separator after every 4 seats
            if (idx > 0 && idx % 6 === 4) {
                seatsHtml += `<div class="rn-aisle">AISLE</div>`;
            }

            seatsHtml += `
                <div class="rn-seat-item ${statusClass}" 
                     data-seat-id="${s.id}" 
                     data-seat-num="${s.seatNumber}" 
                     data-berth="${s.berthType}"
                     onclick="toggleSeatSelection(this, ${s.id}, ${s.seatNumber}, '${s.berthType}')">
                    <span class="rn-seat-number">${s.seatNumber}</span>
                    <span class="rn-seat-type">${s.berthTypeName ? s.berthTypeName.substring(0, 2) : 'ST'}</span>
                </div>
            `;
        });

        grid.innerHTML = seatsHtml;

        // Update counts
        document.getElementById('coachAvailSeatsCount').textContent = data.availableCount;
        document.getElementById('coachBookedSeatsCount').textContent = data.bookedCount;
        updateSelectedSeatsSummary();
    } catch (err) {
        grid.innerHTML = `<div class="alert alert-danger">${err.message}</div>`;
    }
}

function toggleSeatSelection(element, seatId, seatNumber, berthType) {
    if (element.classList.contains('booked')) return;

    const passengerRows = document.querySelectorAll('.passenger-row');
    const maxSeats = passengerRows.length;

    const index = RailNova.selectedSeats.findIndex(s => s.id === seatId);
    if (index >= 0) {
        // Deselect
        RailNova.selectedSeats.splice(index, 1);
        element.classList.remove('selected');
    } else {
        // Select
        if (RailNova.selectedSeats.length >= maxSeats) {
            showAlert(`You can only select up to ${maxSeats} seats for ${maxSeats} passenger(s).`, 'warning');
            return;
        }
        RailNova.selectedSeats.push({
            id: seatId,
            seatNumber,
            berthType,
            coachCode: RailNova.selectedCoach?.coachCode || 'C1'
        });
        element.classList.add('selected');
    }

    updateSelectedSeatsSummary();
}

function updateSelectedSeatsSummary() {
    const summaryEl = document.getElementById('selectedSeatsDisplay');
    if (!summaryEl) return;

    if (RailNova.selectedSeats.length === 0) {
        summaryEl.textContent = 'None (Auto-allocation)';
    } else {
        const text = RailNova.selectedSeats.map(s => `${s.coachCode}-${s.seatNumber} (${s.berthType})`).join(', ');
        summaryEl.textContent = text;
    }
}

function confirmSeatSelection() {
    updateSelectedSeatsSummary();
    bootstrap.Modal.getInstance(document.getElementById('seatPickerModal')).hide();
    showAlert(`Seats selected: ${RailNova.selectedSeats.length} seat(s) reserved for checkout.`, 'success');
}

// ============================================================================
// Proceed to Checkout & Sandbox Payment
// ============================================================================
async function proceedToBookingSubmission() {
    const passengerRows = document.querySelectorAll('.passenger-row');
    const passengers = [];

    let hasValidationError = false;
    passengerRows.forEach((row, idx) => {
        const name = row.querySelector('.pass-name')?.value.trim();
        const age = parseInt(row.querySelector('.pass-age')?.value);
        const gender = row.querySelector('.pass-gender')?.value;
        const berth = row.querySelector('.pass-berth')?.value;

        if (!name || isNaN(age)) {
            hasValidationError = true;
            return;
        }

        const seat = RailNova.selectedSeats[idx];

        passengers.push({
            fullName: name,
            age: age,
            gender: gender,
            berthPreference: berth === 'NO_PREF' ? null : berth,
            selectedSeatId: seat ? seat.id : null
        });
    });

    if (hasValidationError || passengers.length === 0) {
        showAlert('Please fill in complete details for all passengers.', 'danger');
        return;
    }

    const payload = {
        trainId: RailNova.activeTrain.id,
        journeyDate: document.getElementById('searchJourneyDate')?.value,
        coachType: RailNova.selectedClass,
        passengers: passengers
    };

    const submitBtn = document.getElementById('proceedToPaymentBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Reserving Tickets...';

    try {
        // 1. Create Booking in PENDING state
        const bookingRes = await fetchApi('/api/bookings', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        RailNova.currentBooking = bookingRes.data;

        // Hide booking modal
        bootstrap.Modal.getInstance(document.getElementById('bookingModal')).hide();

        // 2. Open Official Sandbox Payment Checkout Modal
        await launchPaymentCheckout(RailNova.currentBooking);
    } catch (err) {
        showAlert(err.message || 'Error creating booking', 'danger');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = 'Proceed to Payment <i class="bi bi-credit-card ms-1"></i>';
    }
}

async function launchPaymentCheckout(booking) {
    const payModal = new bootstrap.Modal(document.getElementById('paymentModal'));
    payModal.show();

    // Set checkout details
    document.getElementById('payAmountDisplay').textContent = `₹${booking.totalAmount}`;
    document.getElementById('payBookingRefDisplay').textContent = booking.bookingReference;
    document.getElementById('payPnrDisplay').textContent = booking.pnrNumber;

    // Create payment order
    try {
        const orderRes = await fetchApi(`/api/payments/create-order?bookingId=${booking.id}`, {
            method: 'POST'
        });

        const order = orderRes.data;
        document.getElementById('payOrderIdDisplay').textContent = order.orderId;
        document.getElementById('payKeyIdDisplay').textContent = order.keyId;

        // Store order details on modal
        document.getElementById('paymentModal').dataset.orderId = order.orderId;
        document.getElementById('paymentModal').dataset.bookingId = booking.id;
    } catch (err) {
        showAlert('Error creating payment order: ' + err.message, 'danger', 'paymentAlertBox');
    }
}

async function executeSandboxPayment(paymentMethod) {
    const modalEl = document.getElementById('paymentModal');
    const orderId = modalEl.dataset.orderId;
    const bookingId = modalEl.dataset.bookingId;

    if (!orderId || !bookingId) {
        showAlert('Invalid order details', 'danger', 'paymentAlertBox');
        return;
    }

    const payBtn = document.getElementById('executePayBtn');
    payBtn.disabled = true;
    payBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Verifying Payment Signature...';

    try {
        // Simulate Sandbox Payment Id
        const paymentId = 'pay_rn_' + Date.now() + Math.floor(Math.random() * 900 + 100);

        // Fetch cryptographic HMAC signature from sandbox endpoint
        const sigRes = await fetchApi(`/api/payments/sandbox-sign?orderId=${orderId}&paymentId=${paymentId}`);
        const signature = sigRes.data.signature;

        // Submit server-side signature verification
        const verifyRes = await fetchApi('/api/payments/verify', {
            method: 'POST',
            body: JSON.stringify({
                bookingId: parseInt(bookingId),
                orderId: orderId,
                paymentId: paymentId,
                signature: signature,
                paymentMethod: paymentMethod || 'UPI'
            })
        });

        // Payment Verified & Confirmed!
        RailNova.currentBooking = verifyRes.data;

        bootstrap.Modal.getInstance(modalEl).hide();
        showAlert('Payment Verified! Ticket confirmed with PNR ' + RailNova.currentBooking.pnrNumber, 'success');

        // Render confirmed E-Ticket
        displayConfirmedETicket(RailNova.currentBooking);
    } catch (err) {
        showAlert(err.message || 'Payment verification failed', 'danger', 'paymentAlertBox');
    } finally {
        payBtn.disabled = false;
        payBtn.innerHTML = 'Pay Now & Confirm Ticket';
    }
}

// ============================================================================
// E-Ticket Display & Printing
// ============================================================================
function displayConfirmedETicket(booking) {
    showSection('ticketSection');

    document.getElementById('ticketPnr').textContent = booking.pnrNumber;
    document.getElementById('ticketRef').textContent = booking.bookingReference;
    document.getElementById('ticketTrainNumber').textContent = booking.trainNumber;
    document.getElementById('ticketTrainName').textContent = booking.trainName;
    document.getElementById('ticketTrainType').textContent = booking.trainTypeName;

    document.getElementById('ticketSource').textContent = `${booking.sourceStationName} (${booking.sourceStationCode})`;
    document.getElementById('ticketDest').textContent = `${booking.destinationStationName} (${booking.destinationStationCode})`;
    document.getElementById('ticketDepTime').textContent = booking.departureTime;
    document.getElementById('ticketArrTime').textContent = booking.arrivalTime;
    document.getElementById('ticketJourneyDate').textContent = formatDate(booking.journeyDate);
    document.getElementById('ticketClass').textContent = `${booking.coachType} - ${booking.coachTypeName}`;

    document.getElementById('ticketBaseFare').textContent = `₹${booking.baseAmount}`;
    document.getElementById('ticketGst').textContent = `₹${booking.taxAmount}`;
    document.getElementById('ticketServiceCharge').textContent = `₹${booking.serviceCharge}`;
    document.getElementById('ticketTotalFare').textContent = `₹${booking.totalAmount}`;
    document.getElementById('ticketTxnId').textContent = booking.paymentId || 'TXN-PAID';

    // Passengers Table
    const tbody = document.getElementById('ticketPassengersTableBody');
    tbody.innerHTML = '';

    booking.passengers.forEach((p, idx) => {
        tbody.innerHTML += `
            <tr>
                <td>${idx + 1}</td>
                <td class="fw-bold">${p.fullName}</td>
                <td>${p.age} / ${p.gender}</td>
                <td><span class="badge bg-primary px-2">${p.assignedCoach}</span></td>
                <td class="fw-bold">${p.assignedSeat}</td>
                <td>${p.assignedBerth}</td>
                <td><span class="badge bg-success">CONFIRMED (CNF)</span></td>
            </tr>
        `;
    });

    // Generate Dynamic SVG QR Code
    renderQrCode(booking);

    // Setup cancel button listener
    const cancelBtn = document.getElementById('ticketCancelBtn');
    if (cancelBtn) {
        cancelBtn.onclick = () => initiateTicketCancellation(booking.id);
    }
}

function renderQrCode(booking) {
    const qrContainer = document.getElementById('ticketQrCodeContainer');
    if (!qrContainer) return;

    // Render clean high-resolution SVG verification matrix
    qrContainer.innerHTML = `
        <svg width="110" height="110" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
            <rect width="100" height="100" fill="#ffffff" />
            <rect x="10" y="10" width="24" height="24" fill="#0052d4" />
            <rect x="14" y="14" width="16" height="16" fill="#ffffff" />
            <rect x="18" y="18" width="8" height="8" fill="#0052d4" />

            <rect x="66" y="10" width="24" height="24" fill="#0052d4" />
            <rect x="70" y="14" width="16" height="16" fill="#ffffff" />
            <rect x="74" y="18" width="8" height="8" fill="#0052d4" />

            <rect x="10" y="66" width="24" height="24" fill="#0052d4" />
            <rect x="14" y="70" width="16" height="16" fill="#ffffff" />
            <rect x="18" y="74" width="8" height="8" fill="#0052d4" />

            <rect x="42" y="15" width="8" height="8" fill="#0052d4" />
            <rect x="42" y="35" width="16" height="8" fill="#0052d4" />
            <rect x="15" y="45" width="8" height="12" fill="#0052d4" />
            <rect x="45" y="55" width="12" height="12" fill="#0052d4" />
            <rect x="65" y="45" width="8" height="18" fill="#0052d4" />
            <rect x="75" y="75" width="12" height="12" fill="#0052d4" />
            <rect x="55" y="75" width="10" height="10" fill="#0052d4" />
        </svg>
    `;
}

function printTicket() {
    window.print();
}

// ============================================================================
// PNR Status Enquiry
// ============================================================================
async function handlePnrSearch(e) {
    e.preventDefault();
    const pnrInput = document.getElementById('pnrSearchInput').value.trim();
    if (!pnrInput) return;

    const resCard = document.getElementById('pnrResultCard');
    resCard.innerHTML = '<div class="text-center p-4"><div class="spinner-border text-primary"></div></div>';
    resCard.classList.remove('d-none');

    try {
        const res = await fetchApi(`/api/pnr/${pnrInput}`);
        const pnr = res.data;

        let statusBadge = '<span class="badge bg-success fs-6">CONFIRMED</span>';
        if (pnr.bookingStatus === 'CANCELLED') statusBadge = '<span class="badge bg-danger fs-6">CANCELLED</span>';
        else if (pnr.bookingStatus === 'REFUNDED') statusBadge = '<span class="badge bg-info fs-6">REFUNDED</span>';

        let passHtml = '';
        pnr.passengers.forEach((p, idx) => {
            passHtml += `
                <tr>
                    <td>${idx + 1}</td>
                    <td class="fw-bold">${p.fullName}</td>
                    <td><span class="badge bg-primary">${p.assignedCoach}</span></td>
                    <td class="fw-bold">${p.assignedSeat}</td>
                    <td>${p.assignedBerth}</td>
                    <td>${statusBadge}</td>
                </tr>
            `;
        });

        resCard.innerHTML = `
            <div class="card p-4 rounded-4 shadow-sm border-0">
                <div class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom flex-wrap gap-2">
                    <div>
                        <span class="text-muted small">PNR Number</span>
                        <h3 class="fw-bold text-primary mb-0">${pnr.pnrNumber}</h3>
                    </div>
                    <div>
                        ${statusBadge}
                    </div>
                </div>

                <div class="row g-3 mb-3">
                    <div class="col-md-4">
                        <span class="text-muted small">Train</span>
                        <div class="fw-bold">${pnr.trainName} (#${pnr.trainNumber})</div>
                    </div>
                    <div class="col-md-4">
                        <span class="text-muted small">Journey Date</span>
                        <div class="fw-bold">${formatDate(pnr.journeyDate)}</div>
                    </div>
                    <div class="col-md-4">
                        <span class="text-muted small">Class</span>
                        <div class="fw-bold">${pnr.coachType}</div>
                    </div>
                    <div class="col-md-6">
                        <span class="text-muted small">Departure</span>
                        <div class="fw-bold">${pnr.sourceStation} at ${pnr.departureTime}</div>
                    </div>
                    <div class="col-md-6">
                        <span class="text-muted small">Arrival</span>
                        <div class="fw-bold">${pnr.destinationStation} at ${pnr.arrivalTime}</div>
                    </div>
                </div>

                <h6 class="fw-bold mt-2">Passenger Information</h6>
                <div class="table-responsive">
                    <table class="table table-sm align-middle table-bordered">
                        <thead class="table-light">
                            <tr><th>#</th><th>Passenger</th><th>Coach</th><th>Seat</th><th>Berth</th><th>Status</th></tr>
                        </thead>
                        <tbody>${passHtml}</tbody>
                    </table>
                </div>
            </div>
        `;
    } catch (err) {
        resCard.innerHTML = `
            <div class="alert alert-warning p-4 rounded-4">
                <i class="bi bi-search me-2"></i> ${err.message}
            </div>
        `;
    }
}

// ============================================================================
// Cancellation & Refund Handling
// ============================================================================
async function initiateTicketCancellation(bookingId) {
    if (!confirm('Are you sure you want to cancel this booking? Cancellation charges will be calculated and refund processed back to your original source.')) {
        return;
    }

    try {
        const res = await fetchApi(`/api/bookings/${bookingId}/cancel`, {
            method: 'PUT',
            body: JSON.stringify({ reason: 'Cancelled via customer portal' })
        });

        showAlert('Ticket cancelled successfully! Refund has been initiated.', 'success');
        displayConfirmedETicket(res.data);
    } catch (err) {
        showAlert(err.message || 'Error cancelling booking', 'danger');
    }
}

// ============================================================================
// User Dashboard
// ============================================================================
async function loadUserDashboard() {
    if (!RailNova.token) {
        showAlert('Please log in to view your dashboard.', 'info');
        new bootstrap.Modal(document.getElementById('authModal')).show();
        return;
    }

    showSection('dashboardSection');

    // Populate user profile info
    document.getElementById('dashUserName').textContent = RailNova.user.fullName;
    document.getElementById('dashUserEmail').textContent = RailNova.user.email;
    document.getElementById('dashUserPhone').textContent = RailNova.user.phone || 'Not added';

    // Load Bookings
    try {
        const res = await fetchApi('/api/bookings/my');
        const bookings = res.data || [];

        renderUserBookings(bookings);
    } catch (err) {
        console.error('Error fetching bookings', err);
    }

    // Load Saved Routes
    loadSavedRoutes();

    // Load Notifications
    loadNotifications();
}

function renderUserBookings(bookings) {
    const upcomingContainer = document.getElementById('userUpcomingBookingsList');
    const pastContainer = document.getElementById('userPastBookingsList');

    if (!upcomingContainer || !pastContainer) return;

    const today = new Date().toISOString().split('T')[0];
    const upcoming = bookings.filter(b => b.journeyDate >= today && b.bookingStatus === 'CONFIRMED');
    const past = bookings.filter(b => b.journeyDate < today || b.bookingStatus !== 'CONFIRMED');

    // Upcoming
    if (upcoming.length === 0) {
        upcomingContainer.innerHTML = '<div class="text-muted p-3">No upcoming journeys. Ready to book your next trip?</div>';
    } else {
        upcomingContainer.innerHTML = upcoming.map(b => createBookingSummaryCard(b)).join('');
    }

    // Past / Cancelled
    if (past.length === 0) {
        pastContainer.innerHTML = '<div class="text-muted p-3">No previous booking history.</div>';
    } else {
        pastContainer.innerHTML = past.map(b => createBookingSummaryCard(b)).join('');
    }
}

function createBookingSummaryCard(b) {
    let statusBadge = '<span class="badge bg-success">CONFIRMED</span>';
    if (b.bookingStatus === 'CANCELLED') statusBadge = '<span class="badge bg-danger">CANCELLED</span>';
    else if (b.bookingStatus === 'PENDING') statusBadge = '<span class="badge bg-warning">PENDING PAYMENT</span>';

    return `
        <div class="card p-3 mb-2 rounded-3 border-light shadow-sm">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                <div>
                    <span class="fw-bold fs-6 text-primary">${b.trainName}</span>
                    <span class="badge bg-light text-dark border ms-2">PNR: ${b.pnrNumber}</span>
                </div>
                <div>${statusBadge}</div>
            </div>
            <div class="row g-2 mt-1 small text-muted">
                <div class="col-md-4">
                    <i class="bi bi-calendar3 me-1"></i> ${formatDate(b.journeyDate)}
                </div>
                <div class="col-md-5">
                    ${b.sourceStationName} (${b.departureTime}) <i class="bi bi-arrow-right"></i> ${b.destinationStationName} (${b.arrivalTime})
                </div>
                <div class="col-md-3 text-md-end">
                    <strong class="text-dark">₹${b.totalAmount}</strong> (${b.totalPassengers} Pass.)
                </div>
            </div>
            <div class="mt-2 pt-2 border-top d-flex gap-2 justify-content-end">
                <button class="btn btn-outline-primary btn-sm" onclick="fetchAndDisplayTicket(${b.id})">
                    <i class="bi bi-ticket-perforated me-1"></i>View Ticket
                </button>
            </div>
        </div>
    `;
}

async function fetchAndDisplayTicket(bookingId) {
    try {
        const res = await fetchApi(`/api/bookings/${bookingId}`);
        displayConfirmedETicket(res.data);
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

async function loadSavedRoutes() {
    const container = document.getElementById('savedRoutesList');
    if (!container) return;

    try {
        const res = await fetchApi('/api/users/saved-routes');
        const routes = res.data || [];

        if (routes.length === 0) {
            container.innerHTML = '<div class="text-muted p-2 small">No favourite routes saved yet.</div>';
            return;
        }

        container.innerHTML = routes.map(r => `
            <div class="d-flex justify-content-between align-items-center p-2 mb-2 bg-light rounded-3 border">
                <div>
                    <strong>${r.sourceStation.name}</strong> 
                    <i class="bi bi-arrow-right mx-1 text-primary"></i> 
                    <strong>${r.destinationStation.name}</strong>
                </div>
                <div class="d-flex gap-2">
                    <button class="btn btn-sm btn-outline-primary" onclick="quickSearchRoute('${r.sourceStation.name}', '${r.destinationStation.name}')">
                        Search
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteSavedRoute(${r.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
        `).join('');
    } catch (err) {
        console.error(err);
    }
}

async function deleteSavedRoute(id) {
    try {
        await fetchApi(`/api/users/saved-routes/${id}`, { method: 'DELETE' });
        loadSavedRoutes();
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

async function loadNotifications() {
    const container = document.getElementById('notificationsList');
    if (!container) return;

    try {
        const res = await fetchApi('/api/users/notifications');
        const notifs = res.data || [];

        if (notifs.length === 0) {
            container.innerHTML = '<div class="text-muted p-2 small">No new notifications.</div>';
            return;
        }

        container.innerHTML = notifs.map(n => `
            <div class="p-2 mb-2 rounded-3 border-start border-4 ${n.read ? 'bg-light border-secondary' : 'bg-primary-subtle border-primary'}">
                <div class="d-flex justify-content-between align-items-center">
                    <strong class="small">${n.title}</strong>
                    <span class="small text-muted">${new Date(n.createdAt).toLocaleDateString()}</span>
                </div>
                <div class="small text-muted mt-1">${n.message}</div>
            </div>
        `).join('');
    } catch (err) {
        console.error(err);
    }
}

async function handleProfileUpdate(e) {
    e.preventDefault();
    const fullName = document.getElementById('profileFullName').value.trim();
    const phone = document.getElementById('profilePhone').value.trim();

    try {
        const res = await fetchApi('/api/users/profile', {
            method: 'PUT',
            body: JSON.stringify({ fullName, phone })
        });

        RailNova.user = res.data;
        localStorage.setItem('rn_user', JSON.stringify(RailNova.user));
        updateAuthUI();
        showAlert('Profile updated successfully!', 'success');
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

// ============================================================================
// Admin Dashboard & Analytics
// ============================================================================
async function loadAdminDashboard() {
    if (!RailNova.token || RailNova.user?.role !== 'ROLE_ADMIN') {
        showAlert('Access denied. Administrator privileges required.', 'danger');
        return;
    }

    showSection('adminSection');

    try {
        const res = await fetchApi('/api/admin/stats');
        const stats = res.data;
        RailNova.adminStats = stats;

        // Populate metrics
        document.getElementById('adminStatTotalUsers').textContent = stats.totalUsers;
        document.getElementById('adminStatTotalTrains').textContent = stats.totalTrains;
        document.getElementById('adminStatTodayBookings').textContent = stats.todayBookings;
        document.getElementById('adminStatConfirmed').textContent = stats.confirmedTickets;
        document.getElementById('adminStatCancelled').textContent = stats.cancelledTickets;
        document.getElementById('adminStatRevenue').textContent = `₹${stats.totalRevenue.toLocaleString()}`;
        document.getElementById('adminStatRefunds').textContent = stats.pendingRefunds;

        // Render Charts using Chart.js
        renderAdminCharts(stats);

        // Load Trains Table
        loadAdminTrainsTable();

        // Load Bookings Table
        loadAdminBookingsTable();

        // Load Users Table
        loadAdminUsersTable();
    } catch (err) {
        console.error('Error loading admin stats', err);
    }
}

function renderAdminCharts(stats) {
    if (typeof Chart === 'undefined') return;

    // Monthly Bookings Trend Chart
    const ctxBookings = document.getElementById('adminBookingsChart');
    if (ctxBookings) {
        if (RailNova.charts.bookingsChart) RailNova.charts.bookingsChart.destroy();

        RailNova.charts.bookingsChart = new Chart(ctxBookings, {
            type: 'line',
            data: {
                labels: stats.chartLabels,
                datasets: [{
                    label: 'Bookings Volume',
                    data: stats.chartBookingsData,
                    borderColor: '#0052d4',
                    backgroundColor: 'rgba(0, 82, 212, 0.1)',
                    tension: 0.35,
                    fill: true,
                    pointBackgroundColor: '#0052d4'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } }
            }
        });
    }

    // Train Type Distribution Chart
    const ctxTrain = document.getElementById('adminTrainTypeChart');
    if (ctxTrain) {
        if (RailNova.charts.trainTypeChart) RailNova.charts.trainTypeChart.destroy();

        const labels = Object.keys(stats.trainTypeDistribution);
        const data = Object.values(stats.trainTypeDistribution);

        RailNova.charts.trainTypeChart = new Chart(ctxTrain, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: ['#e11d48', '#b91c1c', '#15803d', '#1d4ed8', '#f59e0b', '#8b5cf6']
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: 'bottom' } }
            }
        });
    }
}

async function loadAdminTrainsTable() {
    const tbody = document.getElementById('adminTrainsTableBody');
    if (!tbody) return;

    try {
        const res = await fetchApi('/api/trains');
        const trains = res.data || [];

        tbody.innerHTML = trains.map(t => `
            <tr>
                <td class="fw-bold">${t.trainNumber}</td>
                <td>${t.trainName}</td>
                <td><span class="badge bg-light text-dark border">${t.trainType}</span></td>
                <td>${t.sourceStation.name} <i class="bi bi-arrow-right"></i> ${t.destinationStation.name}</td>
                <td>${t.departureTime} - ${t.arrivalTime}</td>
                <td>
                    <span class="badge ${t.active ? 'bg-success' : 'bg-danger'}">
                        ${t.active ? 'ACTIVE' : 'INACTIVE'}
                    </span>
                </td>
                <td>
                    <button class="btn btn-sm btn-outline-secondary me-1" onclick="toggleTrainStatus(${t.id})">
                        ${t.active ? 'Deactivate' : 'Activate'}
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteTrain(${t.id})">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-danger">${err.message}</td></tr>`;
    }
}

async function toggleTrainStatus(trainId) {
    try {
        await fetchApi(`/api/admin/trains/${trainId}/toggle-status`, { method: 'PUT' });
        loadAdminTrainsTable();
        showAlert('Train status updated', 'success');
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

async function deleteTrain(trainId) {
    if (!confirm('Are you sure you want to delete this train?')) return;
    try {
        await fetchApi(`/api/admin/trains/${trainId}`, { method: 'DELETE' });
        loadAdminTrainsTable();
        showAlert('Train deleted successfully', 'success');
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

async function handleAddTrain(e) {
    e.preventDefault();
    const payload = {
        trainNumber: document.getElementById('adminTrainNumber').value.trim(),
        trainName: document.getElementById('adminTrainName').value.trim(),
        trainType: document.getElementById('adminTrainType').value,
        sourceStationId: parseInt(document.getElementById('adminTrainSource').value),
        destinationStationId: parseInt(document.getElementById('adminTrainDest').value),
        departureTime: document.getElementById('adminTrainDepTime').value.trim(),
        arrivalTime: document.getElementById('adminTrainArrTime').value.trim(),
        durationMinutes: parseInt(document.getElementById('adminTrainDuration').value),
        runsOnDays: 'Daily',
        active: true,
        description: 'Scheduled superfast express route'
    };

    try {
        await fetchApi('/api/admin/trains', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        bootstrap.Modal.getInstance(document.getElementById('addTrainModal')).hide();
        loadAdminTrainsTable();
        showAlert('Train created successfully with coaches and fares!', 'success');
    } catch (err) {
        showAlert(err.message, 'danger', 'addTrainAlertBox');
    }
}

async function loadAdminBookingsTable() {
    const tbody = document.getElementById('adminBookingsTableBody');
    if (!tbody) return;

    try {
        const res = await fetchApi('/api/admin/bookings');
        const bookings = res.data || [];

        tbody.innerHTML = bookings.map(b => `
            <tr>
                <td class="fw-bold font-monospace">${b.pnrNumber}</td>
                <td>${b.userName || b.userEmail}</td>
                <td>${b.trainName} (#${b.trainNumber})</td>
                <td>${formatDate(b.journeyDate)}</td>
                <td>${b.coachType} (${b.totalPassengers} Pass.)</td>
                <td class="fw-bold">₹${b.totalAmount}</td>
                <td><span class="badge ${b.bookingStatus === 'CONFIRMED' ? 'bg-success' : 'bg-danger'}">${b.bookingStatus}</span></td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-danger">${err.message}</td></tr>`;
    }
}

async function loadAdminUsersTable() {
    const tbody = document.getElementById('adminUsersTableBody');
    if (!tbody) return;

    try {
        const res = await fetchApi('/api/admin/users');
        const users = res.data || [];

        tbody.innerHTML = users.map(u => `
            <tr>
                <td>#${u.id}</td>
                <td class="fw-bold">${u.fullName}</td>
                <td>${u.email}</td>
                <td>${u.phone || '-'}</td>
                <td><span class="badge bg-light text-dark border">${u.role.replace('ROLE_', '')}</span></td>
                <td><span class="badge ${u.active ? 'bg-success' : 'bg-danger'}">${u.active ? 'ACTIVE' : 'INACTIVE'}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-secondary" onclick="toggleUserStatus(${u.id})">
                        ${u.active ? 'Deactivate' : 'Activate'}
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-danger">${err.message}</td></tr>`;
    }
}

async function toggleUserStatus(userId) {
    try {
        await fetchApi(`/api/admin/users/${userId}/toggle-status`, { method: 'PUT' });
        loadAdminUsersTable();
        showAlert('User status updated', 'success');
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

// ============================================================================
// Staff Portal & Support Tickets
// ============================================================================
async function loadStaffPortal() {
    if (!RailNova.token || (RailNova.user?.role !== 'ROLE_STAFF' && RailNova.user?.role !== 'ROLE_ADMIN')) {
        showAlert('Access denied. Staff privileges required.', 'danger');
        return;
    }

    showSection('staffSection');

    // Load customer support tickets
    try {
        const res = await fetchApi('/api/staff/tickets');
        const tickets = res.data || [];

        const tbody = document.getElementById('staffTicketsTableBody');
        if (tbody) {
            tbody.innerHTML = tickets.map(t => `
                <tr>
                    <td>#${t.id}</td>
                    <td class="fw-bold">${t.userName} <span class="small text-muted">(${t.userEmail})</span></td>
                    <td>${t.pnrNumber || '-'}</td>
                    <td><strong>${t.subject}</strong><div class="small text-muted">${t.message}</div></td>
                    <td><span class="badge ${t.status === 'RESOLVED' ? 'bg-success' : 'bg-warning'}">${t.status}</span></td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="openStaffReplyModal(${t.id}, '${t.subject}')">
                            Reply
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (err) {
        console.error(err);
    }
}

function openStaffReplyModal(ticketId, subject) {
    document.getElementById('staffReplyTicketId').value = ticketId;
    document.getElementById('staffReplySubjectDisplay').textContent = subject;
    new bootstrap.Modal(document.getElementById('staffReplyModal')).show();
}

async function submitStaffReply() {
    const ticketId = document.getElementById('staffReplyTicketId').value;
    const reply = document.getElementById('staffReplyText').value.trim();

    if (!reply) return;

    try {
        await fetchApi(`/api/staff/tickets/${ticketId}/reply`, {
            method: 'PUT',
            body: JSON.stringify({ reply })
        });

        bootstrap.Modal.getInstance(document.getElementById('staffReplyModal')).hide();
        loadStaffPortal();
        showAlert('Reply sent to passenger successfully', 'success');
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

async function handleSupportSubmit(e) {
    e.preventDefault();
    const pnrNumber = document.getElementById('supportPnrNumber').value.trim();
    const subject = document.getElementById('supportSubject').value.trim();
    const message = document.getElementById('supportMessage').value.trim();

    try {
        await fetchApi('/api/support/tickets', {
            method: 'POST',
            body: JSON.stringify({ pnrNumber, subject, message })
        });

        bootstrap.Modal.getInstance(document.getElementById('supportTicketModal')).hide();
        showAlert('Support ticket created. Our team will contact you shortly.', 'success');
    } catch (err) {
        showAlert(err.message, 'danger');
    }
}

// ============================================================================
// Section Navigation & Utilities
// ============================================================================
function showSection(sectionId) {
    const sections = ['homeSection', 'dashboardSection', 'ticketSection', 'adminSection', 'staffSection', 'faqSection'];
    sections.forEach(s => {
        const el = document.getElementById(s);
        if (el) {
            if (s === sectionId) el.classList.remove('d-none');
            else el.classList.add('d-none');
        }
    });

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function showAlert(message, type = 'info', containerId = 'globalAlertContainer') {
    const container = document.getElementById(containerId);
    if (!container) return;

    const alertId = `alert_${Date.now()}`;
    const alertHtml = `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show shadow-sm rounded-3" role="alert">
            <i class="bi bi-${type === 'success' ? 'check-circle-fill' : (type === 'danger' ? 'x-circle-fill' : 'info-circle-fill')} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    container.innerHTML = alertHtml;

    setTimeout(() => {
        const el = document.getElementById(alertId);
        if (el) {
            const bsAlert = bootstrap.Alert.getInstance(el) || new bootstrap.Alert(el);
            bsAlert.close();
        }
    }, 6000);
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    try {
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-IN', {
            weekday: 'short',
            day: 'numeric',
            month: 'short',
            year: 'numeric'
        });
    } catch {
        return dateStr;
    }
}
