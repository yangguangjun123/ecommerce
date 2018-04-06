function() {
    var value = { recom: { itemId: null, count: null, weight: null, idKey: null } };
    var idKey = this._id.itemId;
    this.value.recom.forEach( function(r) {
       value.recom.itemId = r.itemId;
       value.recom.count = r.count;
       value.recom.weight = r.weight;
       value.recom.idKey = idKey;
       emit({ itemId: r.itemId }, value);
    });
}