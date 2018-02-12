function() {
    var key = this.data.userId;
    var value = {
        userId: this.data.userId,
        count: 1
    };
    emit( key, value );
}