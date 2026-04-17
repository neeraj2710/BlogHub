// main.js - Simple shared functions

// Theme toggle
function toggleTheme() {
  const currentTheme = document.documentElement.getAttribute("data-theme");
  const newTheme = currentTheme === "dark" ? "light" : "dark";

  document.documentElement.setAttribute("data-theme", newTheme);
  localStorage.setItem("theme", newTheme);

  const themeIcon = document.querySelector(".theme-toggle i, .theme-toggle-login i");
  if (themeIcon) {
    themeIcon.className = newTheme === "dark" ? "fas fa-sun" : "fas fa-moon";
  }
}

// Initialize theme (syncs icon only — data-theme is already set by inline head script)
function initTheme() {
  const savedTheme = localStorage.getItem("theme") || "light";
  document.documentElement.setAttribute("data-theme", savedTheme);

  const themeIcon = document.querySelector(".theme-toggle i, .theme-toggle-login i");
  if (themeIcon) {
    themeIcon.className = savedTheme === "dark" ? "fas fa-sun" : "fas fa-moon";
  }
}

// Auto-initialize theme on every page that includes main.js
document.addEventListener("DOMContentLoaded", initTheme);

// Simple API GET (with session cookies)
async function apiGet(url) {
  const response = await fetch(url, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include' // ← Important: Send cookies with request
  });
  
  if (response.status === 401 || response.status === 403) {
    window.location.href = 'login.html';
    return null;
  }
  
  if (!response.ok) throw new Error('Request failed');
  return await response.json();
}

// Simple API POST (with session cookies)
async function apiPost(url, data) {
  const response = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include', // ← Important: Send cookies with request
    body: JSON.stringify(data)
  });
  
  if (response.status === 401 || response.status === 403) {
    window.location.href = 'login.html';
    return null;
  }
  
  if (!response.ok) throw new Error('Request failed');
  return await response.json();
}

// Simple API DELETE (with session cookies)
async function apiDelete(url) {
  const response = await fetch(url, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include' // ← Important: Send cookies with request
  });
  
  if (response.status === 401 || response.status === 403) {
    window.location.href = 'login.html';
    return null;
  }
  
  if (!response.ok) throw new Error('Request failed');
  return true;
}

// Simple API PUT (with session cookies)
async function apiPut(url, data) {
  const response = await fetch(url, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include', // ← Important: Send cookies with request
    body: JSON.stringify(data)
  });
  
  if (response.status === 401 || response.status === 403) {
    window.location.href = 'login.html';
    return null;
  }
  
  if (!response.ok) throw new Error('Request failed');
  return await response.json();
}

// Show toast notification
function showToast(message, type = "info") {
  const toast = document.getElementById("toast");
  if (!toast) return;

  toast.textContent = message;
  toast.className = `toast ${type} show`;

  setTimeout(() => {
    toast.classList.remove("show");
  }, 3000);
}

// Update navbar with user info
function updateNavbar() {
  const userName = sessionStorage.getItem('userName');
  const userRole = sessionStorage.getItem('userRole');

  if (!userName) return;

  // Add user info to navbar
  const navActions = document.querySelector('.nav-actions');
  if (navActions) {
    const savedTheme = localStorage.getItem('theme') || 'light';
    const iconClass = savedTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    navActions.innerHTML = `
      <button class="theme-toggle" onclick="toggleTheme()">
        <i class="${iconClass}"></i>
      </button>
      <span style="color: var(--text-primary); margin-right: 10px;">
        👤 ${userName} ${userRole === 'ADMIN' ? '(Admin)' : ''}
      </span>
      <button onclick="logout()" class="btn-primary btn-sm">Logout</button>
    `;
  }
}

// Logout function - calls backend to invalidate session
async function logout() {
  try {
    // Call backend logout endpoint to invalidate session
    await fetch('http://localhost:8082/api/auth/logout', {
      method: 'POST',
      credentials: 'include' // Send session cookie
    });
  } catch (error) {
    console.error('Logout error:', error);
  }
  
  // Clear frontend session data
  sessionStorage.clear();
  window.location.href = 'login.html';
}

// Check if user is admin
function isAdmin() {
  return sessionStorage.getItem('userRole') === 'ADMIN';
}

// Hide elements for non-admin users
function applyRoleBasedUI() {
  if (!isAdmin()) {
    // Hide admin-only elements
    document.querySelectorAll('.admin-only').forEach(el => {
      el.style.display = 'none';
    });
  }
}
