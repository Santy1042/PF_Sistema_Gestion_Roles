document.addEventListener('DOMContentLoaded', () => {
    let toastContainer = document.querySelector('.toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }

    window.showToast = function(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.textContent = message;
        toastContainer.appendChild(toast);
        
        // Trigger animation
        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                toast.classList.add('show');
            });
        });

        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    };

    // Check for flash messages
    const flash = sessionStorage.getItem('flashMessage');
    const flashType = sessionStorage.getItem('flashType') || 'success';
    if (flash) {
        showToast(flash, flashType);
        sessionStorage.removeItem('flashMessage');
        sessionStorage.removeItem('flashType');
    }
});
