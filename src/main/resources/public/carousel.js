// carousel.js

// Handle left/right buttons if needed for carousel navigation
document.addEventListener("DOMContentLoaded", function() {
    const carouselContainer = document.querySelector(".carousel-container");
    const items = document.querySelectorAll(".carousel-item");
    let scrollAmount = 0;

    // Optional: Scroll by a fixed width on button click
    const scrollDistance = items[0].offsetWidth + 20;  // 20px is the gap between items

    // Right button
    const rightButton = document.createElement("button");
    rightButton.textContent = "→";
    rightButton.style.position = "absolute";
    rightButton.style.top = "50%";
    rightButton.style.right = "10px";
    rightButton.style.transform = "translateY(-50%)";
    rightButton.style.fontSize = "20px";
    rightButton.style.zIndex = "10";
    carouselContainer.appendChild(rightButton);

    rightButton.addEventListener("click", function() {
        if (scrollAmount < carouselContainer.scrollWidth - carouselContainer.offsetWidth) {
            scrollAmount += scrollDistance;
            carouselContainer.scrollTo({ left: scrollAmount, behavior: "smooth" });
        }
    });

    // Left button
    const leftButton = document.createElement("button");
    leftButton.textContent = "←";
    leftButton.style.position = "absolute";
    leftButton.style.top = "50%";
    leftButton.style.left = "10px";
    leftButton.style.transform = "translateY(-50%)";
    leftButton.style.fontSize = "20px";
    leftButton.style.zIndex = "10";
    carouselContainer.appendChild(leftButton);

    leftButton.addEventListener("click", function() {
        if (scrollAmount > 0) {
            scrollAmount -= scrollDistance;
            carouselContainer.scrollTo({ left: scrollAmount, behavior: "smooth" });
        }
    });
});
