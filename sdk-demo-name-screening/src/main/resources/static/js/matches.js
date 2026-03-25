async function fetchMatches() {
    try {
        const e = document.getElementById("filter");
        const filter = e.options[e.selectedIndex].value;
        const response = await fetch('/rest/matches?filter=' + filter);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        renderMatches(data);
        resetDetails(true);
    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
}


async function loadCounterpart(id) {
    try {
        const response = await fetch('/rest/counterparts/' + id);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        document.getElementById("counterpartName").innerText = currentRow.cells[0].textContent;
        await renderCounterpart(data);
    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
}


function formatDate(date) {
    if (date != null) {
        return new Date(date).toLocaleString('fr-BE')
    } else {
        return "";
    }
}


function renderMatches(records) {
    const tbody = document.getElementById("matchesTable");
    tbody.replaceChildren();
    records.forEach(rec => {
        const row = document.createElement("tr");
        row.innerHTML = `                                          
          <td>${rec.screenedText}</td>
          <td>${rec.matchedText}</td>
          <td>${rec.score ?? ''}</td>
          <td>${rec.level ?? ''}</td>                      
          <td>${rec.profileId}</td>
          <td>${formatDate(rec.lastModified)}</td>
          <td>${rec.decision ?? ''}</td>
          <td hidden="hidden">${rec.counterpartId}</td>
          <td hidden="hidden">${rec.profileDetails}</td>
          <td hidden="hidden">${rec.id}</td>                                                                  
        `;
        tbody.appendChild(row);
    });
}


async function renderCounterpart(data) {
    resetDetails(false);
    // Populate non-empty fields
    for (const key in data) {
        const el = document.getElementById(key);
        if (el) el.textContent = data[key];
    }
}


function resetDetails(profileIncluded) {
    const tbody = document.getElementById("counterpartTable");
    const fields = tbody.getElementsByTagName("span");
    for (let i = 0; i < fields.length; i++) {
        fields[i].textContent = '';
    }
    if (profileIncluded) {
        const profileElem = document.getElementById("profileDetails")
        profileElem.innerHTML = '';
    }
}


async function renderProfile(jsonString) {
    const profileElem = document.getElementById("profileDetails")
    const jsonObj = JSON.parse(jsonString);
    const pretty = JSON.stringify(jsonObj, null, 2);
    profileElem.innerHTML = `<pre>${pretty}</pre>`;
}


async function updateDecision(decision) {
    try {
        const id = currentRow.cells[9].textContent.trim();
        const url = '/rest/matches/' + id + "?decision=" + decision;
        const response = await fetch(url, {method: 'PUT'});
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        currentRow.cells[6].textContent = decision;
        if (decision === 'IGNORE') {
            await whiteListPrompt(id);
        }
    } catch (error) {
        console.error('Failed to update data:', error);
    }
}
