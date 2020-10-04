import { Component, Input } from '@angular/core';
import { BsModalComponent } from './modal';
var BsModalHeaderComponent = /** @class */ (function () {
    function BsModalHeaderComponent(modal) {
        this.modal = modal;
        this.showDismiss = false;
    }
    BsModalHeaderComponent.decorators = [
        { type: Component, args: [{
                    selector: 'bs-modal-header',
                    template: "\n        <div class=\"modal-header\">\n            <button *ngIf=\"showDismiss\" type=\"button\" class=\"close\" aria-label=\"Dismiss\" (click)=\"modal.dismiss()\">\n                <span aria-hidden=\"true\">&times;</span>\n            </button>\n            <ng-content></ng-content>\n        </div>\n    "
                },] },
    ];
    /** @nocollapse */
    BsModalHeaderComponent.ctorParameters = function () { return [
        { type: BsModalComponent, },
    ]; };
    BsModalHeaderComponent.propDecorators = {
        'showDismiss': [{ type: Input },],
    };
    return BsModalHeaderComponent;
}());
export { BsModalHeaderComponent };
//# sourceMappingURL=modal-header.js.map