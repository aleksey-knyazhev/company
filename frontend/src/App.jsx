import { useEffect, useMemo, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { CompanyPanel } from './components/CompanyPanel';
import { EmployeePanel } from './components/EmployeePanel';
import { EmployeeReport } from './components/EmployeeReport';
import { Metric } from './components/common/Metric';
import {
  clearCompanyError,
  createCompany,
  deleteCompany,
  fetchCompanies,
  updateCompany,
} from './store/companiesSlice';
import {
  clearEmployeeError,
  createEmployee,
  deleteEmployee,
  fetchEmployees,
  updateEmployee,
} from './store/employeesSlice';

const emptyCompanyForm = { name: '' };
const emptyEmployeeForm = { name: '', age: '', companyId: '' };

export function App() {
  const dispatch = useDispatch();
  const companies = useSelector((state) => state.companies.items);
  const employees = useSelector((state) => state.employees.items);
  const companiesLoading = useSelector((state) => state.companies.loading);
  const employeesLoading = useSelector((state) => state.employees.loading);
  const companyError = useSelector((state) => state.companies.error);
  const employeeError = useSelector((state) => state.employees.error);

  const [activeCompanyId, setActiveCompanyId] = useState('all');
  const [companyForm, setCompanyForm] = useState(emptyCompanyForm);
  const [companyEditId, setCompanyEditId] = useState('');
  const [employeeForm, setEmployeeForm] = useState(emptyEmployeeForm);
  const [employeeEditId, setEmployeeEditId] = useState('');
  const [companyValidationError, setCompanyValidationError] = useState('');
  const [employeeValidationError, setEmployeeValidationError] = useState('');

  useEffect(() => {
    dispatch(fetchCompanies());
    dispatch(fetchEmployees());
  }, [dispatch]);

  useEffect(() => {
    if (companies.length > 0 && !employeeForm.companyId) {
      setEmployeeForm((current) => ({ ...current, companyId: companies[0].id }));
    }
  }, [companies, employeeForm.companyId]);

  const companyById = useMemo(() => (
    companies.reduce((acc, company) => {
      acc[company.id] = company;
      return acc;
    }, {})
  ), [companies]);

  const visibleEmployees = useMemo(() => {
    if (activeCompanyId === 'all') {
      return employees;
    }
    return employees.filter((employee) => employee.companyId === activeCompanyId);
  }, [activeCompanyId, employees]);

  const employeeCountRows = useMemo(() => {
    const grouped = employees.reduce((acc, employee) => {
      const key = `${employee.companyId}:${employee.age}`;
      const existing = acc[key] || {
        companyId: employee.companyId,
        age: employee.age,
        countEmployee: 0,
      };
      existing.countEmployee += 1;
      acc[key] = existing;
      return acc;
    }, {});

    return Object.values(grouped)
      .map((row) => ({
        ...row,
        companyName: companyById[row.companyId]?.name || 'Не найдена',
      }))
      .sort((first, second) => (
        first.companyName.localeCompare(second.companyName, 'ru') || Number(first.age) - Number(second.age)
      ));
  }, [companyById, employees]);

  const totalAge = employees.reduce((sum, employee) => sum + Number(employee.age || 0), 0);
  const averageAge = employees.length === 0 ? 0 : Math.round(totalAge / employees.length);

  const submitCompany = async (event) => {
    event.preventDefault();
    const name = companyForm.name.trim();
    if (!name) {
      setCompanyValidationError('Введите название компании');
      return;
    }
    setCompanyValidationError('');

    if (companyEditId) {
      await dispatch(updateCompany({ id: companyEditId, name }));
      setCompanyEditId('');
    } else {
      await dispatch(createCompany({ name }));
    }
    setCompanyForm(emptyCompanyForm);
  };

  const submitEmployee = async (event) => {
    event.preventDefault();
    const name = employeeForm.name.trim();
    const ageValue = String(employeeForm.age).trim();
    const age = Number(ageValue);
    const companyId = employeeForm.companyId;
    const errors = [];
    if (!name) {
      errors.push('Введите имя сотрудника');
    }
    if (!ageValue) {
      errors.push('Введите возраст сотрудника');
    }
    if (errors.length > 0) {
      setEmployeeValidationError(errors.join('\n'));
      return;
    }
    if (!companyId) {
      setEmployeeValidationError('Выберите компанию сотрудника');
      return;
    }
    setEmployeeValidationError('');

    if (employeeEditId) {
      await dispatch(updateEmployee({ id: employeeEditId, name, age, companyId }));
      setEmployeeEditId('');
    } else {
      await dispatch(createEmployee({ name, age, companyId }));
    }
    setEmployeeForm({ ...emptyEmployeeForm, companyId: companies[0]?.id || '' });
  };

  const resetCompanyEdit = () => {
    setCompanyEditId('');
    setCompanyForm(emptyCompanyForm);
    setCompanyValidationError('');
  };

  const resetEmployeeEdit = () => {
    setEmployeeEditId('');
    setEmployeeForm({ ...emptyEmployeeForm, companyId: companies[0]?.id || '' });
    setEmployeeValidationError('');
  };

  const startCompanyEdit = (company) => {
    setCompanyEditId(company.id);
    setCompanyForm({ name: company.name });
  };

  const startEmployeeEdit = (employee) => {
    setEmployeeEditId(employee.id);
    setEmployeeForm({
      name: employee.name,
      age: String(employee.age),
      companyId: employee.companyId,
    });
  };

  const removeCompany = async (id) => {
    await dispatch(deleteCompany(id));
    if (activeCompanyId === id) {
      setActiveCompanyId('all');
    }
    if (employeeForm.companyId === id) {
      setEmployeeForm((current) => ({
        ...current,
        companyId: companies.find((company) => company.id !== id)?.id || '',
      }));
    }
  };

  return (
    <main className="app-shell">
      <section className="topbar">
        <div>
          <p className="eyebrow">Company API</p>
          <h1>Компании и сотрудники</h1>
        </div>
        <div className="stats-row">
          <Metric label="Компаний" value={companies.length} />
          <Metric label="Сотрудников" value={employees.length} />
          <Metric label="Средний возраст" value={averageAge} />
        </div>
      </section>

      <section className="workspace-grid">
        <CompanyPanel
          companies={companies}
          companyEditId={companyEditId}
          companyError={companyValidationError || companyError}
          companyForm={companyForm}
          employees={employees}
          loading={companiesLoading}
          onClearError={() => {
            setCompanyValidationError('');
            dispatch(clearCompanyError());
          }}
          onDelete={removeCompany}
          onEdit={startCompanyEdit}
          onFormChange={(form) => {
            setCompanyValidationError('');
            setCompanyForm(form);
          }}
          onResetEdit={resetCompanyEdit}
          onSubmit={submitCompany}
        />

        <EmployeePanel
          activeCompanyId={activeCompanyId}
          companies={companies}
          companyById={companyById}
          employeeEditId={employeeEditId}
          employeeError={employeeValidationError || employeeError}
          employeeForm={employeeForm}
          loading={employeesLoading}
          onClearError={() => {
            setEmployeeValidationError('');
            dispatch(clearEmployeeError());
          }}
          onDelete={(id) => dispatch(deleteEmployee(id))}
          onEdit={startEmployeeEdit}
          onFormChange={(form) => {
            setEmployeeValidationError('');
            setEmployeeForm(form);
          }}
          onResetEdit={resetEmployeeEdit}
          onSelectCompany={setActiveCompanyId}
          onSubmit={submitEmployee}
          visibleEmployees={visibleEmployees}
        />
      </section>

      <EmployeeReport
        loading={companiesLoading || employeesLoading}
        rows={employeeCountRows}
      />
    </main>
  );
}
