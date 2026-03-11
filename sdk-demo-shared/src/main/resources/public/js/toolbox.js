
async function fetchLabelInto(targetElement) {
    try {
        const response = await fetch('/dashboard/metrics');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        data.screening.forEach(metric => {
            if (metric.name === 'count-records') {
                targetElement.textContent = metric.label.toLowerCase();
            }
        });
    } catch (error) {
        console.error('Failed to fetch metrics:', error);
    }
}