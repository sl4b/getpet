const readableValues = {
    species: {
        dog: 'Dog',
        cat: 'Cat',
    },
    size: {
        small: 'Small',
        medium: 'Medium',
        large: 'Large',
    },
    color: {
        black: 'Black',
        white: 'White',
        brown: 'Brown',
        gold: 'Gold',
        dGray: 'Dark Gray',
        lGray: 'Light Gray',
    },
    gender: {
        m: 'Male',
        f: 'Female',
    },
};

const elementsToFind = ['searchForm', 'result', ];
const elements = {};
for (const elementToFind of elementsToFind)
    elements[elementToFind] = document.getElementById(elementToFind);

const readForm = form => {
    values = {};

    for (const option of form.querySelectorAll('.option')) {
        const name = option.getAttribute('name');
        const type = option.getAttribute('type');

        switch (type) {
            case 'radio':
                values[name] = form.elements[name].value !== '' ? form.elements[name].value : null;
                break;
            case 'checkbox':
                values[name] = [];
                for (const checkbox of option.querySelectorAll('input'))
                    if (checkbox.checked)
                        values[name].push(checkbox.value);
                break;
            default:
                values[name] = option.querySelector('input').value;
        }
    }

    return values;
};