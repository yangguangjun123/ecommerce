function () {
    var value = { recom: { itemId: null, count: null, weight: null, idKey: null } };
    value.recom.itemId = this.data.itemId;
    value.recom.weight = this.data.weight;
    emit({ itemId: this.data.itemId }, value);
}