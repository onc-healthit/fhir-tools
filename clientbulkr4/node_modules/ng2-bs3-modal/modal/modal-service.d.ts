import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/fromEvent';
import 'rxjs/add/operator/filter';
import { BsModalComponent } from './modal';
import { BsModalHideType } from './models';
export declare class BsModalService {
    private modals;
    private $body;
    onBackdropClose$: Observable<BsModalHideType>;
    onKeyboardClose$: Observable<BsModalHideType>;
    onModalStack$: Observable<Event>;
    constructor();
    add(modal: BsModalComponent): void;
    remove(modal: BsModalComponent): void;
    focusNext(): void;
    dismissAll(): Promise<{}[]>;
}
