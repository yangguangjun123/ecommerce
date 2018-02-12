function(key, values) {
	var reducedObject = {
        userId: key,
        count: 0
    };
    values.forEach( function(value) {
			reducedObject.count += value.count;
	});
    return reducedObject;
}