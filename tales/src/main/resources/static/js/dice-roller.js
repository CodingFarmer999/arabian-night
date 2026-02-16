function animateRoll(element, valueGenerator) {
    let count = 0;
    // clear previous state
    element.style.color = "#333";
    element.style.transform = "scale(1)";

    const interval = setInterval(() => {
        const val = valueGenerator();
        element.innerText = val === ' ' ? '○' : val; // Animate with random values
        count++;
        if (count > 10) {
            clearInterval(interval);
            // Final result styling
            const finalVal = valueGenerator(); // Get final value
            element.innerText = finalVal;

            element.style.color = "#d9534f"; // Highlight result
            element.style.transform = "scale(1.2)";
            element.style.transition = "transform 0.2s, color 0.2s";

            setTimeout(() => {
                element.style.color = "#333";
                element.style.transform = "scale(1)";
            }, 500);
        }
    }, 50);
}

function rollD6() {
    const display = document.getElementById('d6-display');
    animateRoll(display, () => Math.floor(Math.random() * 6) + 1);
}

function rollSpecial() {
    const display = document.getElementById('special-display');
    const faces = ['(空)', '(空)', '+', '+', '-', '-'];
    animateRoll(display, () => faces[Math.floor(Math.random() * faces.length)]);
}
