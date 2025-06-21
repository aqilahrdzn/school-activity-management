(function ($) {
  'use strict';

  // Check if the canvas exists before rendering the chart
  if ($("#visit-sale-chart").length) {
    const ctx = document.getElementById('visit-sale-chart').getContext("2d");

    // Create vertical gradient for the bars
    const gradient = ctx.createLinearGradient(0, 0, 0, 181);
    gradient.addColorStop(0, 'rgba(218, 140, 255, 1)');
    gradient.addColorStop(1, 'rgba(154, 85, 255, 1)');

    // Fetch data from the server
    $.getJSON("event-stats", function (monthlyData) {
      // Debugging check for null or invalid data
      if (!Array.isArray(monthlyData) || monthlyData.length === 0 || monthlyData.every(d => d === null || d === 0)) {
        console.warn("No data received or data is empty.");
        $('#visit-sale-chart').replaceWith('<p style="text-align:center; color:#888;">No event data available to display.</p>');
        return;
      }

      // Proceed if data is valid
      new Chart(ctx, {
        type: 'bar',
        data: {
          labels: ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'],
          datasets: [{
            label: "Total Events",
            data: monthlyData,
            backgroundColor: gradient,
            borderColor: gradient,
            borderWidth: 1,
            hoverBackgroundColor: 'rgba(154, 85, 255, 0.9)',
            barPercentage: 0.6,
            categoryPercentage: 0.6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: "Number of Events"
              },
              ticks: {
                stepSize: 1
              }
            },
            x: {
              title: {
                display: true,
                text: "Month"
              }
            }
          },
          plugins: {
            legend: {
              display: true,
              labels: {
                color: '#555'
              }
            },
            tooltip: {
              callbacks: {
                label: function (context) {
                  return `${context.dataset.label}: ${context.parsed.y}`;
                }
              }
            }
          }
        }
      });
    }).fail(function () {
      console.error("Failed to fetch data from the server.");
      $('#visit-sale-chart').replaceWith('<p style="text-align:center; color:#f00;">Failed to load chart data.</p>');
    });
  }

})(jQuery);




  if ($("#traffic-chart").length) {
  const ctx = document.getElementById('traffic-chart').getContext('2d');

  fetch('/SchoolActivityManagementSystem/event-category-count')
    .then(response => response.json())
    .then(data => {
      const gradient1 = ctx.createLinearGradient(0, 0, 0, 181);
      gradient1.addColorStop(0, 'rgba(54, 215, 232, 1)');
      gradient1.addColorStop(1, 'rgba(177, 148, 250, 1)');

      const gradient2 = ctx.createLinearGradient(0, 0, 0, 181);
      gradient2.addColorStop(0, 'rgba(6, 185, 157, 1)');
      gradient2.addColorStop(1, 'rgba(132, 217, 210, 1)');

      const gradient3 = ctx.createLinearGradient(0, 0, 0, 181);
      gradient3.addColorStop(0, 'rgba(255, 191, 150, 1)');
      gradient3.addColorStop(1, 'rgba(254, 112, 150, 1)');

      new Chart(ctx, {
        type: 'doughnut',
        data: {
          labels: data.labels,
          datasets: [{
            data: data.counts,
            backgroundColor: [gradient1, gradient2, gradient3],
            borderColor: [gradient1, gradient2, gradient3],
            hoverBackgroundColor: [gradient1, gradient2, gradient3]
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { display: true }
          }
        }
      });
    })
    .catch(error => console.error("Error loading chart data:", error));



  if ($("#inline-datepicker").length) {
    $('#inline-datepicker').datepicker({
      enableOnReadonly: true,
      todayHighlight: true,
    });
  }
  if ($.cookie('purple-pro-banner') != "true") {
    document.querySelector('#proBanner').classList.add('d-flex');
    document.querySelector('.navbar').classList.remove('fixed-top');
  } else {
    document.querySelector('#proBanner').classList.add('d-none');
    document.querySelector('.navbar').classList.add('fixed-top');
  }

  if ($(".navbar").hasClass("fixed-top")) {
    document.querySelector('.page-body-wrapper').classList.remove('pt-0');
    document.querySelector('.navbar').classList.remove('pt-5');
  } else {
    document.querySelector('.page-body-wrapper').classList.add('pt-0');
    document.querySelector('.navbar').classList.add('pt-5');
    document.querySelector('.navbar').classList.add('mt-3');

  }
  document.querySelector('#bannerClose').addEventListener('click', function () {
    document.querySelector('#proBanner').classList.add('d-none');
    document.querySelector('#proBanner').classList.remove('d-flex');
    document.querySelector('.navbar').classList.remove('pt-5');
    document.querySelector('.navbar').classList.add('fixed-top');
    document.querySelector('.page-body-wrapper').classList.add('proBanner-padding-top');
    document.querySelector('.navbar').classList.remove('mt-3');
    var date = new Date();
    date.setTime(date.getTime() + 24 * 60 * 60 * 1000);
    $.cookie('purple-pro-banner', "true", {
      expires: date
    });
  });
}