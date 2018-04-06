function() {
    var key = this.data.userId;
    var value = {
        userId: this.data.userId,
        items: this.data.itemId
    };
    emit( key, value );
}