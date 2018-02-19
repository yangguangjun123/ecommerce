function() {
    var value = { recom: { itemId: null, count: null, weight: null } };
    value.recom.itemId = this._id.b;
    value.recom.count = this.value;
    emit( { itemId: this._id.a }, value );
    if(this._id.b != this._id.a) {
        value.recom.itemId = this._id.a;
        value.recom.count = this.value;
        emit( { itemId: this._id.b }, value );
    }
}