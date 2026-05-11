import { EmptyState } from './common/EmptyState';
import { PanelHeader } from './common/PanelHeader';

export function EmployeeReport({ loading, rows }) {
  return (
    <section className="panel report-panel">
      <PanelHeader
        title="Сотрудники по компаниям и возрасту"
        loading={loading}
        actionLabel="Результат CTE-запроса"
      />
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Компания</th>
              <th>Возраст</th>
              <th className="right">Количество</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={`${row.companyId}:${row.age}`}>
                <td>{row.companyName}</td>
                <td>{row.age}</td>
                <td className="right">{row.countEmployee}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {rows.length === 0 && <EmptyState title="Нет данных для отчёта" />}
      </div>
    </section>
  );
}
