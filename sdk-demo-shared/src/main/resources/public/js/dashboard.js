
function renderMetrics(containerId, metrics) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';

    metrics.forEach(metric => {

        // This one is more a param than a metric :-/
        if (metric.name === 'window-size') {
            document.getElementById("throttlingWindowSize").innerHTML = metric.value + " " + metric.unit;
            return;
        }

        const box = document.createElement('div');
        box.className = 'metric-box';

        const name = document.createElement('div');
        name.className = 'metric-name';
        name.textContent = metric.label;

        const value = document.createElement('div');
        value.className = 'metric-value';

        const unit = document.createElement('div');
        unit.className = 'metric-unit';

        switch (metric.unit) {
            case "%":
                value.textContent = formatNumber(metric.value, 1);
                unit.textContent = metric.unit;
                break
            case "duration":
                value.textContent = formatDuration(metric.value);
                unit.textContent = "";
                break
            default:
                value.textContent = formatNumber(metric.value, 0);
                unit.textContent = metric.unit;
        }

        box.appendChild(name);
        box.appendChild(value);
        box.appendChild(unit);

        if (metric.isGauge) {

            const {T0, T1, T2, Tm} = metric.thresholds;
            const E1 = 1; // Math.max(T0, 5);
            const E2 = T2 * 1.1; // Math.max(T2, Tm);

            setColor(metric, value, T1, T2);

            const wrapper = document.createElement('div');
            wrapper.className = 'gauge-wrapper';

            const gauge = document.createElement('div');
            gauge.className = 'gauge';

            // Main needle (current value)
            const mainNeedle = document.createElement('div');
            mainNeedle.className = 'needle';
            const mainPercent = Math.max(0, Math.min((metric.value - E1) / (E2 - E1), 1));
            mainNeedle.style.left = `${mainPercent * 100}%`;
            gauge.appendChild(mainNeedle);

            // T1 dashed needle
            const needleT1 = document.createElement('div');
            needleT1.className = 'threshold-needle';
            const t1Percent = Math.max(0, Math.min((T1 - E1) / (E2 - E1), 1));
            needleT1.style.left = `${t1Percent * 100}%`;
            gauge.appendChild(needleT1);

            // T2 dashed needle
            const needleT2 = document.createElement('div');
            needleT2.className = 'threshold-needle';
            const t2Percent = Math.max(0, Math.min((T2 - E1) / (E2 - E1), 1));
            needleT2.style.left = `${t2Percent * 100}%`;
            gauge.appendChild(needleT2);

            // Add the gauge bar
            wrapper.appendChild(gauge);

            // Add scale labels
            const labels = document.createElement('div');
            labels.className = 'gauge-labels';
            const leftLabel = formatNumber(E1,0) + " ms";
            const rightLabel = formatNumber(T2,0) + " ms";
            labels.innerHTML = `<span>${leftLabel}</span><span>${rightLabel}</span>`;
            wrapper.appendChild(labels);

            // Append to box
            box.appendChild(wrapper);
        }

        container.appendChild(box);
    });
}


function formatNumber(number, fractionDigits) {
    return  number.toLocaleString('fr-BE', {
        minimumFractionDigits: fractionDigits,
        maximumFractionDigits: fractionDigits,
    });
}

const formatDuration = sec => {
    const time = {
        h: Math.floor(sec / 3600) % 24,
        m: Math.floor(sec / 60) % 60,
        s: Math.floor(sec) % 60
    };
    return Object.entries(time)
        .filter(val => val[1] !== 0)
        .map(([key, val]) => `${val}${key}`)
        .join(' ');
};

function setColor(metric, div, lowerLimit, upperLimit) {
    let color;
    if ( metric.value<lowerLimit) {
        color = "green";
    } else if ( metric.value<upperLimit) {
        color = "orange";
    } else {
        color = "black"
    }
    div.classList.add(`${color}-value`);
}


async function fetchMetrics() {
    try {
        const response = await fetch('/dashboard/metrics');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        renderMetrics('screeningMetrics', data.screening);
        renderMetrics('throttlingMetrics', data.throttling);
        renderMetrics('jobMetrics', data.springbatch);
    } catch (error) {
        console.error('Failed to fetch metrics:', error);
    }
}