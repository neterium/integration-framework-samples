async function fetchCounterparts() {
    try {
        const response = await fetch('/rest/counterparts');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        renderCounterparts(data);
    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
}

async function fetchMatches(counterpartId) {
    try {
        const response = await fetch('/rest/counterparts/'+counterpartId+'/matches');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        renderMatches(data);
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


function renderCounterparts(records) {
    const tbody = document.getElementById("counterpartsTable");
    tbody.replaceChildren();
    records.forEach(rec => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${rec.id}</td>              
          <td>${rec.lastName}</td>         
          <td>${rec.firstName}</td>
          <td>${rec.dateOfBirth?? ''}</td>               
          <td>${rec.gender}</td>
          <td>${formatDate(rec.lastImport)}</td>
          <td>${formatDate(rec.lastScreenedAt)}</td>
          <td class="number">${formatNumber(rec.matchCount, 0, 0)}</td>
          <td class="number">${formatNumber(rec.ignoredCount, 0, 0)}</td>
          <td class="number">${formatNumber(rec.alertCount, 0, 0)}</td>         
        `;
        let color;
        if (rec.matchCount===0) {
            color = "black";
        } else if (rec.alertCount===0) {
            color = "green";
        } else if ( rec.alertCount < rec.matchCount) {
            color = "orange";
        } else {
            color = "red"
        }
        row.classList.add(`${color}-value`);
        tbody.appendChild(row);
    });
}

function renderMatches(records) {
    const tbody = document.getElementById("matchesTable");
    tbody.replaceChildren();
    records.forEach(rec => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${rec.externalId}</td>                                
          <td>${rec.profileId}</td>       
          <td>${rec.matchedText}</td>
          <td>${rec.score??''}</td>
          <td>${rec.level??''}</td>               
          <td>${rec.checkSum??''}</td>        
          <td>${formatDate(rec.lastModified)}</td>
          <td><select id="decision_${rec.id}" onchange="updateDecision('${rec.id}')">
                <option value="NONE">?</option>
                <option value="IGNORE">IGNORE</option>
                <option value="KEEP">CONFIRM</option>
                <option value="FORWARD">FORWARD</option>
              </select>                                   
          </td>   
        `;
        tbody.appendChild(row);
        // Set selected value based on rec.decision
        const selectElement = document.getElementById(`decision_${rec.id}`);
        if (selectElement) {
            selectElement.value = rec.decision || "";
        }
    });
}

async function updateDecision(id) {
    try {
        const e = document.getElementById("decision_"+id);
        const url = '/rest/matches/'+id + "?decision=" + e.options[e.selectedIndex].value;
        const response = await fetch(url,{method: 'PUT'});
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        await fetchCounterparts();
    } catch (error) {
        console.error('Failed to update data:', error);
    }
}
