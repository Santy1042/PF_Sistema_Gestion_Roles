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

        requestAnimationFrame(() => {
            requestAnimationFrame(() => {
                toast.classList.add('show');
            });
        });

        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => {
                toast.remove();
            }, 300);
        }, 3000);
    };

    window.showNotification = window.showToast;

    const flash = sessionStorage.getItem('flashMessage');
    const flashType = sessionStorage.getItem('flashType') || 'success';
    if (flash) {
        showToast(flash, flashType);
        sessionStorage.removeItem('flashMessage');
        sessionStorage.removeItem('flashType');
    }
});
