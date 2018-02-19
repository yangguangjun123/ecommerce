function() {
    var value = { recom: { itemId: null, count: null, weight: null} };
    this.value.recom.forEach( function(r) {
        value.recom.itemId = r.itemId;
        value.recom.count = r.count;
        value.recom.weight = r.weight;
        if(typeof r.idKey == 'string' && r.idKey.length) {
            emit({ itemId: r.idKey }, value);
        }
    });
}