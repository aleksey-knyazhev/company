export function Alert({ message, onClose }) {
  return (
    <div className="alert">
      <span>{message}</span>
      <button type="button" onClick={onClose}>Закрыть</button>
    </div>
  );
}
