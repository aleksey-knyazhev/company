export function PanelHeader({ title, loading, actionLabel }) {
  return (
    <div className="panel-header">
      <div>
        <h2>{title}</h2>
        <p>{loading ? 'Загрузка данных' : actionLabel}</p>
      </div>
    </div>
  );
}
