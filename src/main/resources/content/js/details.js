requirePrivilegeLevel('any');

const fillDetails = response => {
    const setRadio = (elementId, name) => {
        const element = document.getElementById(elementId);
        element.querySelector('input[value="' + name + '"]').checked = true;
    };
    const setCheckboxes = (elementId, names) => {
        const element = document.getElementById(elementId);
        for (const name of names) {
            element.querySelector('input[value="' + name + '"]').checked = true;
        }
    };

    const animal = response.animal;

    if (animal) {
        document.getElementById('optionName').value = animal.name;
        document.getElementById('optionBreed').value = animal.breed;
        document.getElementById('optionWeight').value = animal.weight;
        document.getElementById('optionIntakeNumber').value = animal.intakeNumber;
        setRadio('optionSize', animal.size);
        setRadio('optionGender', animal.gender);
        setRadio('optionSpecies', animal.species);
        setCheckboxes('optionColor', animal.colors);
        document.getElementById('optionVaccinated').checked = true;
        document.getElementById('optionSpayNeuter').checked = true;
    }
}

const searchParams = new URLSearchParams(window.location.search);

if (!searchParams.has('intakeNumber')) {
    // TODO: This shows exactly no information to the user that something has gone wrong
    console.error("Internal error");
} else {
    const intakeNumber = parseInt(searchParams.get('intakeNumber'));

    fetch("/persistence/animal", {
        method: 'POST',
        body: JSON.stringify({ intakeNumber }),
    })
        .then(resp => resp.json())
        .then(fillDetails)
        .catch(e => console.log("ERROR: " + e));
}