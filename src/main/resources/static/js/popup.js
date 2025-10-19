// Custom Popup Dialog Component
class MoktobPopup {
    constructor() {
        this.createPopupHTML();
        this.bindEvents();
    }

    createPopupHTML() {
        const popupHTML = `
            <div id="moktob-popup-overlay" class="popup-overlay" style="display: none;">
                <div class="popup-container">
                    <div class="popup-header">
                        <h5 class="popup-title" id="popup-title">Title</h5>
                        <button type="button" class="popup-close" id="popup-close">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                    <div class="popup-body" id="popup-body">
                        Content goes here
                    </div>
                    <div class="popup-footer" id="popup-footer">
                        <button type="button" class="btn btn-secondary" id="popup-cancel">Cancel</button>
                        <button type="button" class="btn btn-primary" id="popup-confirm">Confirm</button>
                    </div>
                </div>
            </div>
        `;
        
        // Add popup to body if it doesn't exist
        if (!document.getElementById('moktob-popup-overlay')) {
            document.body.insertAdjacentHTML('beforeend', popupHTML);
        }
    }

    bindEvents() {
        const overlay = document.getElementById('moktob-popup-overlay');
        const closeBtn = document.getElementById('popup-close');
        const cancelBtn = document.getElementById('popup-cancel');
        const confirmBtn = document.getElementById('popup-confirm');

        // Close popup when clicking overlay
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                this.hide();
            }
        });

        // Close popup when clicking close button
        closeBtn.addEventListener('click', () => {
            this.hide();
        });

        // Cancel button
        cancelBtn.addEventListener('click', () => {
            this.hide();
        });

        // Confirm button
        confirmBtn.addEventListener('click', () => {
            if (this.onConfirm) {
                this.onConfirm();
            }
        });

        // Close on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && overlay.style.display !== 'none') {
                this.hide();
            }
        });
    }

    show(options = {}) {
        const {
            title = 'Confirmation',
            message = 'Are you sure?',
            type = 'confirm', // 'confirm', 'alert', 'success', 'error'
            confirmText = 'Confirm',
            cancelText = 'Cancel',
            onConfirm = null,
            onCancel = null,
            showCancel = true
        } = options;

        const overlay = document.getElementById('moktob-popup-overlay');
        const titleEl = document.getElementById('popup-title');
        const bodyEl = document.getElementById('popup-body');
        const footerEl = document.getElementById('popup-footer');
        const confirmBtn = document.getElementById('popup-confirm');
        const cancelBtn = document.getElementById('popup-cancel');

        // Set title
        titleEl.textContent = title;

        // Set message
        bodyEl.innerHTML = message;

        // Set button text
        confirmBtn.textContent = confirmText;
        cancelBtn.textContent = cancelText;

        // Show/hide cancel button
        cancelBtn.style.display = showCancel ? 'inline-block' : 'none';

        // Set button styles based on type
        confirmBtn.className = 'btn';
        switch (type) {
            case 'success':
                confirmBtn.classList.add('btn-success');
                break;
            case 'error':
                confirmBtn.classList.add('btn-danger');
                break;
            case 'alert':
                confirmBtn.classList.add('btn-warning');
                break;
            default:
                confirmBtn.classList.add('btn-primary');
        }

        // Store callbacks
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;

        // Show popup
        overlay.style.display = 'flex';
        document.body.style.overflow = 'hidden'; // Prevent background scrolling
    }

    hide() {
        const overlay = document.getElementById('moktob-popup-overlay');
        overlay.style.display = 'none';
        document.body.style.overflow = ''; // Restore scrolling
        
        // Clear callbacks
        this.onConfirm = null;
        this.onCancel = null;
    }

    // Convenience methods
    confirm(options) {
        this.show({ ...options, type: 'confirm' });
    }

    alert(options) {
        this.show({ ...options, type: 'alert', showCancel: false });
    }

    success(options) {
        this.show({ ...options, type: 'success', showCancel: false });
    }

    error(options) {
        this.show({ ...options, type: 'error', showCancel: false });
    }
}

// Create global instance
window.MoktobPopup = new MoktobPopup();

// Add CSS styles
const popupCSS = `
    .popup-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
    }

    .popup-container {
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        max-width: 500px;
        width: 90%;
        max-height: 80vh;
        overflow: hidden;
        animation: popupSlideIn 0.3s ease-out;
    }

    @keyframes popupSlideIn {
        from {
            opacity: 0;
            transform: scale(0.9) translateY(-20px);
        }
        to {
            opacity: 1;
            transform: scale(1) translateY(0);
        }
    }

    .popup-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 24px 16px;
        border-bottom: 1px solid #e9ecef;
    }

    .popup-title {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 600;
        color: #212529;
    }

    .popup-close {
        background: none;
        border: none;
        font-size: 1.5rem;
        color: #6c757d;
        cursor: pointer;
        padding: 0;
        width: 30px;
        height: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        transition: background-color 0.2s;
    }

    .popup-close:hover {
        background-color: #f8f9fa;
        color: #495057;
    }

    .popup-body {
        padding: 20px 24px;
        color: #495057;
        line-height: 1.5;
    }

    .popup-footer {
        padding: 16px 24px 20px;
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        border-top: 1px solid #e9ecef;
        background-color: #f8f9fa;
    }

    .popup-footer .btn {
        min-width: 80px;
    }
`;

// Add CSS to head
const style = document.createElement('style');
style.textContent = popupCSS;
document.head.appendChild(style);
