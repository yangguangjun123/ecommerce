function (key, values) {
    var reducedObject = { recom: [] };

    values.forEach( function(value) {
        reducedObject.recom = reducedObject.recom.concat(value.recom)
    });

    var weight = {};
    reducedObject.recom.forEach( function(value) {
        if(value.weight > 0 && typeof value.itemId == 'string'
                            && value.itemId.length) {
                weight[value.itemId] = value.weight;
        }
    });

    reducedObject.recom.forEach( function(value) {
        if(typeof value.idKey == 'string' && value.idKey.length) {
            value.weight = weight[value.itemId];
        }
    });

    return reducedObject;
}