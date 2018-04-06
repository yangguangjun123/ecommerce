function(key, values) {
	var reducedObject = {
        userId: key,
        items: []
    };
    values.forEach( function(value) {
			reducedObject.items = reducedObject.items.concat(value.items)
	});
    return reducedObject;
}