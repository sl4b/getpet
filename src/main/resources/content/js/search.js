requirePrivilegeLevel('any');

const displaySearchResults = results => {
    const resultElement = document.getElementById('result');
    while (resultElement.firstChild)
        resultElement.removeChild(resultElement.firstChild);

    console.log('Got response with results', results);

    for (const result of results.results) {
        const resultDiv = document.createElement('div');
        resultDiv.classList.add('result');

        const animalInfo = document.createElement('div');
        animalInfo.classList.add('animalInfo');
        resultDiv.appendChild(animalInfo);

        const animalName = document.createElement('div');
        animalName.classList.add('animalName');
        animalInfo.appendChild(animalName);

        const nameAttributeName = document.createElement('span');
        nameAttributeName.classList.add('animalAttributeName');
        nameAttributeName.innerText = 'Name:';
        animalName.appendChild(nameAttributeName);

        animalName.appendChild(document.createTextNode(' '));

        const nameAttributeValue = document.createElement('span');
        nameAttributeValue.classList.add('animalAttributeValue');
        nameAttributeValue.innerText = result.name;
        animalName.appendChild(nameAttributeValue);

        const animalSubInfo = document.createElement('div');
        animalSubInfo.classList.add('animalSubInfo');
        animalInfo.appendChild(animalSubInfo);

        const leftAttributePair = document.createElement('div');
        leftAttributePair.classList.add('animalAttributePair');
        leftAttributePair.classList.add('leftAttributePair');
        animalSubInfo.appendChild(leftAttributePair);

        const breedAttributeName = document.createElement('span');
        breedAttributeName.classList.add('animalAttributeName');
        breedAttributeName.innerText = 'Breed:';
        leftAttributePair.appendChild(breedAttributeName);

        leftAttributePair.appendChild(document.createTextNode(' '));

        const breedAttributeValue = document.createElement('span');
        breedAttributeValue.classList.add('animalAttributeValue');
        breedAttributeValue.innerText = result.breed;
        leftAttributePair.appendChild(breedAttributeValue);

        const leftBr = document.createElement('br');
        leftAttributePair.appendChild(leftBr);

        const colorAttributeName = document.createElement('span');
        colorAttributeName.classList.add('animalAttributeName');
        colorAttributeName.innerText = 'Color:';
        leftAttributePair.appendChild(colorAttributeName);

        leftAttributePair.appendChild(document.createTextNode(' '));

        const colorAttributeValue = document.createElement('span');
        colorAttributeValue.classList.add('animalAttributeValue');
        colorAttributeValue.innerText = result.colors.map(val => readableValues.color[val]).join(', ');
        leftAttributePair.appendChild(colorAttributeValue);

        const rightAttributePair = document.createElement('div');
        rightAttributePair.classList.add('animalAttributePair');
        rightAttributePair.classList.add('rightAttributePair');
        animalSubInfo.appendChild(rightAttributePair);

        const sizeAttributeName = document.createElement('span');
        sizeAttributeName.classList.add('animalAttributeName');
        sizeAttributeName.innerText = 'Size:';
        rightAttributePair.appendChild(sizeAttributeName);

        rightAttributePair.appendChild(document.createTextNode(' '));

        const sizeAttributeValue = document.createElement('span');
        sizeAttributeValue.classList.add('animalAttributeValue');
        sizeAttributeValue.innerText = readableValues.size[result.size];
        rightAttributePair.appendChild(sizeAttributeValue);

        const rightBr = document.createElement('br');
        rightAttributePair.appendChild(rightBr);

        const genderAttributeName = document.createElement('span');
        genderAttributeName.classList.add('animalAttributeName');
        genderAttributeName.innerText = 'Gender:';
        rightAttributePair.appendChild(genderAttributeName);

        rightAttributePair.appendChild(document.createTextNode(' '));

        const genderAttributeValue = document.createElement('span');
        genderAttributeValue.classList.add('animalAttributeValue');
        genderAttributeValue.innerText = readableValues.gender[result.gender];
        rightAttributePair.appendChild(genderAttributeValue);

        const reclaimButton = document.createElement('button');
        reclaimButton.classList.add('button');
        reclaimButton.innerText = 'Reclaim';
        reclaimButton.onclick = () => {
            window.location = window.location.origin
                + '/reclamation.html'
                + `?intakeNumber=${result.intakeNumber}`;            
        };
        resultDiv.appendChild(reclaimButton);

        const adoptButton = document.createElement('button');
        adoptButton.classList.add('button');
        adoptButton.innerText = 'Adopt';
        adoptButton.onclick = () => {
            window.location = window.location.origin
                + '/adoption.html'
                + `?intakeNumber=${result.intakeNumber}`;            
        };
        resultDiv.appendChild(adoptButton);

        const detailsButton = document.createElement('button');
        detailsButton.classList.add('button');
        detailsButton.innerText = 'Details';
        detailsButton.onclick = () => {
            window.location = window.location.origin
                + '/details.html'
                + `?intakeNumber=${result.intakeNumber}`;            
        };
        resultDiv.appendChild(detailsButton);

        resultElement.appendChild(resultDiv);
    }
}

const onSubmit = ev => {
    ev.preventDefault();

    let searchRequest = { query: readForm(document.getElementById('searchForm')) };

    apiCall({
        endpoint: '/search',
        method: 'POST',
        body: searchRequest,
    })
        .then(displaySearchResults)
        .catch(e => displayError('failed to perform search', e));

    return false;
}

document.getElementById('searchForm').addEventListener('submit', onSubmit);

// special functionality... allow other pages to link to this one and search by cage number
const cageNumber = new URLSearchParams(window.location.search).get('cageNumber');
if (cageNumber) {
    document.getElementById('optionCageNumber').value = cageNumber;
    document.querySelector('.button.submit').click();
}