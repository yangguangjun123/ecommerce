function(key, values) {
    var reducedObject = { recom: [] };

    values.forEach( function(value) {
        reducedObject.recom = reducedObject.recom.concat(value.recom)
    });

    reducedObject.recom.sort(function(a, b) {
        return b.count - a.count;
    });

    return reducedObject;
}