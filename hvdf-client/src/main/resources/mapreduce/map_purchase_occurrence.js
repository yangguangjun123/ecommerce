function() {
	for (i = 0; i < this.value.items.length; i++) {
		for (j = i + 1; j <= this.value.items.length; j++) {
			if (typeof this.value.items[j] != 'undefined') {
				emit({a: this.value.items[i] ,b: this.value.items[j] }, 1);
			}
		}
	}
}