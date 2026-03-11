async function fetchData() {
    try {
        const response = await fetch('/dashboard/last-job');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        renderSteps(data);
    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
}


function formatNumber(number, fractionDigits) {
    if (number != null) {
        return number.toLocaleString('fr-BE', {
            minimumFractionDigits: fractionDigits,
            maximumFractionDigits: fractionDigits,
        });
    } else {
        return "";
    }
}

function formatDate(date) {
    if (date != null) {
        return new Date(date).toLocaleString('fr-BE')
    } else {
        return "";
    }
}


function renderSteps(steps) {
    const tbody = document.getElementById("stepTable");
    tbody.replaceChildren();
    steps.forEach(step => {
        const row = document.createElement("tr");
        row.innerHTML = `      
          <td class="badge-container"><div class="badge">${step.type}</div></td>
          <td>${step.name}</td>
          <td>${formatDate(step.start)}</td>
          <td>${formatDate(step.end)}</td>
          <td>${step.duration}</td>
          <td><span class="status status-${step.status.toLowerCase()}">${step.status}</span></td>
          <td class="number">${formatNumber(step.read, 0, 0)}</td>
          <td class="number">${formatNumber(step.write, 0, 0)}</td>
          <td class="number">${formatNumber(step.skipped, 0, 0)}</td>
        `;
        tbody.appendChild(row);
    });
}
