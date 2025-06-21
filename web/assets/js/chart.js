$(function () {
  /* ChartJS
   * -------
   * Data and config for chartjs
   */
  'use strict';
  $(document).ready(function () {
  const ctx = document.getElementById("barChart").getContext("2d");
  let barChart;

  function loadEventData(year = 2025) {
    $.ajax({
      url: "/SchoolActivityManagementSystem/events-per-month?year=" + year, // Gantikan "YourAppName" dengan context path sebenar
      method: "GET",
      dataType: "json",
      success: function (data) {
        const chartData = {
          labels: [
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
          ],
          datasets: [{
            label: "Events Created",
            data: data,
            backgroundColor: "rgba(54, 162, 235, 0.5)",
            borderColor: "rgba(54, 162, 235, 1)",
            borderWidth: 1
          }]
        };

        const chartOptions = {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                precision: 0
              }
            }
          }
        };

        if (barChart) {
          barChart.data = chartData;
          barChart.update();
        } else {
          barChart = new Chart(ctx, {
            type: "bar",
            data: chartData,
            options: chartOptions
          });
        }
      },
      error: function (xhr, status, error) {
        console.error("Failed to load data:", error);
      }
    });
  }

  // Initial load
  loadEventData();

  // Auto-refresh every 10 seconds
  setInterval(() => {
    loadEventData();
  }, 10000);
});

  
  if ($("#barChart").length) {
    var barChartCanvas = $("#barChart").get(0).getContext("2d");
    // This will get the first returned node in the jQuery collection.
    var barChart = new Chart(barChartCanvas, {
      type: 'bar',
      data: barChartData,
      options: options
    });
  }

});