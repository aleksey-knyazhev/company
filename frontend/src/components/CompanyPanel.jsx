import { Alert } from './common/Alert';
import { EmptyState } from './common/EmptyState';
import { PanelHeader } from './common/PanelHeader';

export function CompanyPanel({
  activeCompanyId,
  companies,
  companyEditId,
  companyError,
  companyForm,
  employees,
  loading,
  onClearError,
  onDelete,
  onEdit,
  onFormChange,
  onResetEdit,
  onSelectCompany,
  onSubmit,
}) {
  return (
    <div className="panel">
      <PanelHeader
        title="Компании"
        loading={loading}
        actionLabel={companyEditId ? 'Изменить' : 'Добавить'}
      />
      {companyError && (
        <Alert message={companyError} onClose={onClearError} />
      )}
      <form className="form-row" onSubmit={onSubmit}>
        <input
          value={companyForm.name}
          onChange={(event) => onFormChange({ name: event.target.value })}
          placeholder="Название компании"
          aria-label="Название компании"
        />
        <button type="submit">{companyEditId ? 'Сохранить' : 'Создать'}</button>
        {companyEditId && (
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

      <div className="list">
        {companies.length === 0 && <EmptyState title="Компаний пока нет" />}
        {companies.map((company) => {
          const count = employees.filter((employee) => employee.companyId === company.id).length;
          return (
            <article className="list-item" key={company.id}>
              <div>
                <h3>{company.name}</h3>
                <p>{count} сотрудников</p>
              </div>
              <div className="actions">
                <button type="button" className="icon-button" title="Редактировать" onClick={() => onEdit(company)}>
                  Edit
                </button>
                <button type="button" className="icon-button danger" title="Удалить" onClick={() => onDelete(company.id)}>
                  Del
                </button>
              </div>
            </article>
          );
        })}
      </div>
    </div>
  );
}
