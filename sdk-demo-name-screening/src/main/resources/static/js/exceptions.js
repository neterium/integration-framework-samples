async function fetchExceptions() {
    try {
        const response = await fetch('/rest/exceptions?limit=500');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        renderExceptions(data);
    } catch (error) {
        console.error('Failed to fetch data:', error);
    }
}


async function deleteException(id) {
    try {
        const response = await fetch('/rest/exceptions/' + id, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        window.location.reload();
    } catch (error) {
        console.error('Failed to delete exception:', error);
    }
}


async function whiteListPrompt(matchId) {
    try {
        const ok = confirm("Do you also want to create a 'white-list' exception for this match ?");
        if (ok) {
            const formData = new FormData();
            formData.append('matchId', matchId);
            const response = await fetch('/rest/exceptions', {
                method: 'POST',
                body: formData
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        }
    } catch (error) {
        console.error('Failed to update data:', error);
    }
}


function formatDate(date) {
    if (date != null) {
        return new Date(date).toLocaleString('fr-BE')
    } else {
        return "";
    }
}


function renderExceptions(records) {
    const tbody = document.getElementById("exceptionsTable");
    tbody.replaceChildren();
    records.forEach(rec => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${rec.id}</td>              
          <td>${rec.profileId}</td>         
          <td>${rec.expirationType}</td>
          <td>${rec.status}</td>                   
          <td>${formatDate(rec.created)}</td>        
          <td><input type="button" onclick="deleteException('${rec.id}')" value="Delete"></td>         
        `;
        tbody.appendChild(row);
    });
}


