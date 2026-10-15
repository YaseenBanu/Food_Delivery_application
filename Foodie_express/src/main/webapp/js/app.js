/* ===========================================================
   FOODIE EXPRESS — app.js
   Shared micro-interactions. Include on every page:
   <script src="js/app.js"></script>
   =========================================================== */

document.addEventListener("DOMContentLoaded", function () {

  /* ---- Sticky navbar shadow on scroll ---- */
  var navbar = document.querySelector(".navbar");
  if (navbar) {
    window.addEventListener("scroll", function () {
      navbar.classList.toggle("scrolled", window.scrollY > 8);
    });
  }

  /* ---- Button ripple on click ---- */
  document.querySelectorAll(".btn").forEach(function (btn) {
    btn.addEventListener("click", function (e) {
      var rect = btn.getBoundingClientRect();
      var ripple = document.createElement("span");
      var size = Math.max(rect.width, rect.height);
      ripple.className = "ripple";
      ripple.style.width = ripple.style.height = size + "px";
      ripple.style.left = (e.clientX - rect.left - size / 2) + "px";
      ripple.style.top = (e.clientY - rect.top - size / 2) + "px";
      btn.appendChild(ripple);
      setTimeout(function () { ripple.remove(); }, 550);
    });
  });

  /* ---- Show a spinner on the submit button of any form while it posts
     to the servlet, so the UI doesn't feel frozen on the DB round-trip ---- */
  document.querySelectorAll("form").forEach(function (form) {
    form.addEventListener("submit", function () {
      var submitBtn = form.querySelector('button[type="submit"]');
      if (submitBtn) submitBtn.classList.add("is-loading");
    });
  });

  /* ---- Highlight the active bottom-nav tab based on current page ---- */
  var page = window.location.pathname.split("/").pop() || "home.html";
  document.querySelectorAll(".bottom-nav a").forEach(function (link) {
    if (link.getAttribute("href") === page) link.classList.add("active");
  });

});

/* ---- Toast helper: call toast("Added to cart", "success") from anywhere ---- */
function toast(message, type) {
  var stack = document.getElementById("toast-stack");
  if (!stack) {
    stack = document.createElement("div");
    stack.id = "toast-stack";
    document.body.appendChild(stack);
  }
  var el = document.createElement("div");
  el.className = "toast" + (type ? " " + type : "");
  var icon = type === "success" ? "ti-circle-check" : type === "error" ? "ti-alert-circle" : "ti-info-circle";
  el.innerHTML = '<i class="ti ' + icon + '"></i><span>' + message + "</span>";
  stack.appendChild(el);
  setTimeout(function () { el.remove(); }, 3000);
}

/* ---- Cart bar helper: call updateCartBar(3, 549) to show/refresh it ---- */
function updateCartBar(itemCount, total) {
  var bar = document.getElementById("cart-bar");
  if (!bar) return;
  if (itemCount > 0) {
    bar.querySelector(".cart-count").textContent = itemCount + (itemCount === 1 ? " item" : " items");
    bar.querySelector(".cart-total").textContent = "₹" + total;
    bar.classList.add("visible");
  } else {
    bar.classList.remove("visible");
  }
}