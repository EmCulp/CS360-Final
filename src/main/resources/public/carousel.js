document.addEventListener("DOMContentLoaded", function() {
    const carouselContainer = document.querySelector(".carousel-container");
    const items = document.querySelectorAll(".carousel-item");
    let currentIndex = 0;

    const scrollDistance = items[0].offsetWidth;  // We want to scroll by the full width of an item

    const rightButton = document.createElement("button");
    rightButton.textContent = "→";
    rightButton.style.position = "absolute";
    rightButton.style.top = "50%";
    rightButton.style.right = "10px";
    rightButton.style.transform = "translateY(-50%)";
    rightButton.style.fontSize = "20px";
    rightButton.style.zIndex = "10";
    carouselContainer.appendChild(rightButton);

    const leftButton = document.createElement("button");
    leftButton.textContent = "←";
    leftButton.style.position = "absolute";
    leftButton.style.top = "50%";
    leftButton.style.left = "10px";
    leftButton.style.transform = "translateY(-50%)";
    leftButton.style.fontSize = "20px";
    leftButton.style.zIndex = "10";
    carouselContainer.appendChild(leftButton);

    function updateButtons() {
        if (currentIndex === 0) {
            leftButton.style.display = "none";  // Hide left button on the first item
        } else {
            leftButton.style.display = "block";  // Show left button
        }

        if (currentIndex === items.length - 1) {
            rightButton.style.display = "none";  // Hide right button on the last item
        } else {
            rightButton.style.display = "block";  // Show right button
        }
    }

    rightButton.addEventListener("click", function() {
        if (currentIndex < items.length - 1) {
            currentIndex++;
            document.querySelector(".carousel-inner").style.transform = `translateX(-${currentIndex * scrollDistance}px)`;
            updateButtons();
        }
    });

    leftButton.addEventListener("click", function() {
        if (currentIndex > 0) {
            currentIndex--;
            document.querySelector(".carousel-inner").style.transform = `translateX(-${currentIndex * scrollDistance}px)`;
            updateButtons();
        }
    });

    updateButtons();  // Initialize buttons on page load
});
