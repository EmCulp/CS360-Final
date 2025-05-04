document.addEventListener("DOMContentLoaded", function() {
    const carouselContainer = document.querySelector(".carousel-container");
    const items = document.querySelectorAll(".carousel-item");
    let currentIndex = 0;

    const scrollDistance = items[0].offsetWidth;  // We want to scroll by the full width of an item

    const rightButton = document.getElementById("nextBtn");
    const leftButton = document.getElementById("prevBtn");

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

    function updateCarousel(){
        document.querySelector(".carousel-inner").style.transform = `translateX(-${currentIndex * scrollDistance}px)`;
    }

    function nextBook(){
        if(currentIndex < items.length - 1){
            currentIndex++;
            updateCarousel();
            updateButtons();
        }
    }

    function prevBook(){
        if(currentIndex > 0){
            currentIndex--;
            updateCarousel();
            updateButtons();
        }
    }

    rightButton.addEventListener("click", nextBook);
    leftButton.addEventListener("click", prevBook);

    updateButtons();  // Initialize buttons on page load
});
