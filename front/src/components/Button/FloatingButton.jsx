import plus from '../../assets/images/plus.png';
import hanger from '../../assets/images/clothes-hanger.png';

export default function FloatingButton({ onAddClick }) {
    return (
        <button
            className="flex items-center justify-center gap-1.5 fixed bottom-2 right-0 mb-20 mr-3 p-2 bg-primary text-white rounded-lg shadow-lg w-16 h-10"
            onClick={onAddClick} // 클릭 이벤트 핸들러 props 수신
        >
            <img src={plus} alt="플러스" className="h-4 w-4" />
            <img src={hanger} alt="옷걸이그림" className="h-auto w-6" />
        </button>
    );
}