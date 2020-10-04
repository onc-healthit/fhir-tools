import { Component, Input } from '@angular/core';
import { BsModalComponent } from './modal';
var BsModalFooterComponent = /** @class */ (function () {
    function BsModalFooterComponent(modal) {
        this.modal = modal;
        this.showDefaultButtons = false;
        this.dismissButtonLabel = 'Dismiss';
        this.closeButtonLabel = 'Close';
    }
    BsModalFooterComponent.decorators = [
        { type: Component, args: [{
                    selector: 'bs-modal-footer',
                    template: "\n        <div class=\"modal-footer\">\n            <ng-content></ng-content>\n            <button *ngIf=\"showDefaultButtons\" type=\"button\" class=\"btn btn-default\" (click)=\"modal.dismiss()\">\n                {{dismissButtonLabel}}\n            </button>\n            <button *ngIf=\"showDefaultButtons\" type=\"button\" class=\"btn btn-primary\" (click)=\"modal.close()\">\n                {{closeButtonLabel}}\n              </button>\n        </div>\n    "
                },] },
    ];
    /** @nocollapse */
    BsModalFooterComponent.ctorParameters = function () { return [
        { type: BsModalComponent, },
    ]; };
    BsModalFooterComponent.propDecorators = {
        'showDefaultButtons': [{ type: Input },],
        'dismissButtonLabel': [{ type: Input },],
        'closeButtonLabel': [{ type: Input },],
    };
    return BsModalFooterComponent;
}());
export { BsModalFooterComponent };
//# sourceMappingURL=modal-footer.js.map