// Typing text effect
document.addEventListener('DOMContentLoaded', () => {
    const el = document.querySelector('.typing-text');
    if (!el) return;

    const text = el.getAttribute('data-text');
    let index = 0;

    function type() {
        if (index <= text.length) {
            el.textContent = text.slice(0, index);
            index++;
            setTimeout(type, 80);
        } else {
            setTimeout(() => {
                index = 0;
                type();
            }, 3000);
        }
    }
    type();
});
