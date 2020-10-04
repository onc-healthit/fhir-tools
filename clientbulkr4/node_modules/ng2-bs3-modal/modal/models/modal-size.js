var BsModalSize = /** @class */ (function () {
    function BsModalSize() {
    }
    BsModalSize.isValidSize = function (size) {
        return size && (size === BsModalSize.Small || size === BsModalSize.Large);
    };
    BsModalSize.Small = 'sm';
    BsModalSize.Large = 'lg';
    return BsModalSize;
}());
export { BsModalSize };
//# sourceMappingURL=modal-size.js.map