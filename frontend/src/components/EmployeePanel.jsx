import { Alert } from './common/Alert';
import { EmptyState } from './common/EmptyState';
import { PanelHeader } from './common/PanelHeader';

export function EmployeePanel({
  activeCompanyId,
  companies,
  companyById,
  employeeEditId,
  employeeError,
  employeeForm,
  loading,
  onClearError,
  onDelete,
  onEdit,
  onFormChange,
  onResetEdit,
  onSelectCompany,
  onSubmit,
  visibleEmployees,
}) {
  return (
    <div className="panel wide">
      <PanelHeader
        title="Сотрудники"
        loading={loading}
        actionLabel={employeeEditId ? 'Изменить' : 'Добавить'}
      />
      {employeeError && (
        <Alert message={employeeError} onClose={onClearError} />
      )}
      <form className="employee-form" onSubmit={onSubmit}>
        <input
          value={employeeForm.name}
          onChange={(event) => onFormChange({ ...employeeForm, name: event.target.value })}
          placeholder="Имя сотрудника"
          aria-label="Имя сотрудника"
        />
        <input
          value={employeeForm.age}
          onChange={(event) => onFormChange({ ...employeeForm, age: event.target.value })}
          type="number"
          min="1"
          max="120"
          placeholder="Возраст"
          aria-label="Возраст"
        />
        <select
          value={employeeForm.companyId}
          onChange={(event) => onFormChange({ ...employeeForm, companyId: event.target.value })}
          aria-label="Компания сотрудника"
        >
          <option value="" disabled>Компания</option>
          {companies.map((company) => (
            <option key={company.id} value={company.id}>{company.name}</option>
          ))}
        </select>
        <button type="submit" disabled={companies.length === 0}>
          {employeeEditId ? 'Сохранить' : 'Создать'}
        </button>
        {employeeEditId && (
          <button type="button" className="button-muted" onClick={onResetEdit}>
            Отмена
          </button>
        )}
      </form>

      <div className="filter-strip">
        <button
          type="button"
          className={activeCompanyId === 'all' ? 'chip active' : 'chip'}
          onClick={() => onSelectCompany('all')}
        >
          Все
        </button>
        {companies.map((company) => (
          <button
            type="button"
            key={company.id}
            className={activeCompanyId === company.id ? 'chip active' : 'chip'}
            onClick={() => onSelectCompany(company.id)}
          >
            {company.name}
          </button>
        ))}
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Имя</th>
              <th>Возраст</th>
              <th>Компания</th>
              <th className="right">Действия</th>
            </tr>
          </thead>
          <tbody>
            {visibleEmployees.map((employee) => (
              <tr key={employee.id}>
                <td>{employee.name}</td>
                <td>{employee.age}</td>
                <td>{companyById[employee.companyId]?.name || 'Не найдена'}</td>
                <td className="right">
                  <button type="button" className="icon-button" title="Редактировать" onClick={() => onEdit(employee)}>
                    Edit
                  </button>
                  <button type="button" className="icon-button danger" title="Удалить" onClick={() => onDelete(employee.id)}>
                    Del
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {visibleEmployees.length === 0 && <EmptyState title="Сотрудников по фильтру нет" />}
      </div>
    </div>
  );
}
