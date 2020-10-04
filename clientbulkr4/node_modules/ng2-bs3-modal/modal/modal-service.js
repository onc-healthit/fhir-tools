import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/fromEvent';
import 'rxjs/add/operator/filter';
import { BsModalHideType } from './models';
var EVENT_SUFFIX = 'ng2-bs3-modal';
var KEYUP_EVENT_NAME = "keyup." + EVENT_SUFFIX;
var CLICK_EVENT_NAME = "click." + EVENT_SUFFIX;
var SHOW_EVENT_NAME = "show.bs.modal." + EVENT_SUFFIX;
var BsModalService = /** @class */ (function () {
    function BsModalService() {
        var _this = this;
        this.modals = [];
        this.$body = jQuery(document.body);
        this.onBackdropClose$ = Observable.fromEvent(this.$body, CLICK_EVENT_NAME)
            .filter(function (e) { return jQuery(e.target).is('.modal'); })
            .map(function () { return BsModalHideType.Backdrop; })
            .share();
        this.onKeyboardClose$ = Observable.fromEvent(this.$body, KEYUP_EVENT_NAME)
            .filter(function (e) { return e.which === 27; })
            .map(function () { return BsModalHideType.Keyboard; })
            .share();
        this.onModalStack$ = Observable.fromEvent(this.$body, SHOW_EVENT_NAME)
            .do(function () {
            var zIndex = 1040 + (10 * $('.modal:visible').length);
            $(_this).css('z-index', zIndex);
            setTimeout(function () {
                $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
            }, 0);
        })
            .share();
    }
    BsModalService.prototype.add = function (modal) {
        this.modals.push(modal);
    };
    BsModalService.prototype.remove = function (modal) {
        var index = this.modals.indexOf(modal);
        if (index > -1)
            this.modals.splice(index, 1);
    };
    BsModalService.prototype.focusNext = function () {
        var visible = this.modals.filter(function (m) { return m.visible; });
        if (visible.length) {
            this.$body.addClass('modal-open');
            visible[visible.length - 1].focus();
        }
    };
    BsModalService.prototype.dismissAll = function () {
        return Promise.all(this.modals.map(function (m) {
            return m.dismiss();
        }));
    };
    BsModalService.decorators = [
        { type: Injectable },
    ];
    /** @nocollapse */
    BsModalService.ctorParameters = function () { return []; };
    return BsModalService;
}());
export { BsModalService };
//# sourceMappingURL=modal-service.js.map