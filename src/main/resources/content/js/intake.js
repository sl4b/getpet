requirePrivilegeLevel('any');

const onSubmit = ev => {
    ev.preventDefault();

    let intakeRequest = { animal: readForm(document.getElementById('intakeForm')) };

    delete intakeRequest.animal.intakeNumber;

    apiCall({
        endpoint: `/animal/new`,
        method: 'POST',
        body: intakeRequest,
    })
    .then(resp => window.history.back())
    .catch(e => displayError('Failed to intake animal', e));
}

// Enable onSubmit
document.getElementById('intakeForm').addEventListener('submit', onSubmit);