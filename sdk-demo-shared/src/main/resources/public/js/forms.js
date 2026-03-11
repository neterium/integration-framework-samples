
async function submitFormUsingGet(form, redirectUrl) {
    const formData = new URLSearchParams(new FormData(form));
    const url = form.action + '?' + formData.toString();
    await doFetch(url, {
            method: 'GET'
        },
        redirectUrl);
}

async function submitFormUsingPost(form, redirectUrl) {
    const formData = new FormData(form);
    const url = form.action;
    await doFetch(url,{
            method: 'POST',
            body: formData
        },
        redirectUrl);
 }


async function doFetch(url, init, redirectUrl) {
    try {
        const response = await fetch(url, init);
        if (response.ok) {
            window.location.href = redirectUrl;
        } else {
            alert('Request failed with status: ' + response.status);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error while submitting the form');
    }

}
