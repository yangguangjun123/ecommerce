function(key, values) {
    var reducedObject = { recom: [] };
    values.forEach( function(value) {
        reducedObject.recom = reducedObject.recom.concat(value.recom)
    });
    return reducedObject;
}