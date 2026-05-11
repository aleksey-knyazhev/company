import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { Provider, useDispatch, useSelector } from 'react-redux';
import {
  configureStore,
  createAsyncThunk,
  createSlice,
} from '@reduxjs/toolkit';
import './styles.css';

const requestJson = async (url, options = {}) => {
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  });

  if (!response.ok) {
    const message = response.status === 404
      ? 'Запись не найдена'
      : `Ошибка запроса: ${response.status}`;
    throw new Error(message);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
};

const fetchCompanies = createAsyncThunk('companies/fetchAll', () => requestJson('/companies'));
const createCompany = createAsyncThunk('companies/create', (payload) => requestJson('/companies', {
  method: 'POST',
  body: JSON.stringify(payload),
}));
const updateCompany = createAsyncThunk('companies/update', ({ id, name }) => requestJson(`/companies/${id}`, {
  method: 'PUT',
  body: JSON.stringify({ name }),
}));
const deleteCompany = createAsyncThunk('companies/delete', async (id) => {
  await requestJson(`/companies/${id}`, { method: 'DELETE' });
  return id;
});

const fetchEmployees = createAsyncThunk('employees/fetchAll', () => requestJson('/employees'));
const createEmployee = createAsyncThunk('employees/create', (payload) => requestJson('/employees', {
  method: 'POST',
  body: JSON.stringify(payload),
}));
const updateEmployee = createAsyncThunk('employees/update', ({ id, name, age, companyId }) => requestJson(`/employees/${id}`, {
  method: 'PUT',
  body: JSON.stringify({ name, age, companyId }),
}));
const deleteEmployee = createAsyncThunk('employees/delete', async (id) => {
  await requestJson(`/employees/${id}`, { method: 'DELETE' });
  return id;
});

const companiesSlice = createSlice({
  name: 'companies',
  initialState: {
    items: [],
    loading: false,
    error: '',
  },
  reducers: {
    clearCompanyError: (state) => {
      state.error = '';
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCompanies.pending, (state) => {
        state.loading = true;
        state.error = '';
      })
      .addCase(fetchCompanies.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchCompanies.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(createCompany.fulfilled, (state, action) => {
        state.items.push(action.payload);
      })
      .addCase(updateCompany.fulfilled, (state, action) => {
        state.items = state.items.map((company) => (
          company.id === action.payload.id ? action.payload : company
        ));
      })
      .addCase(deleteCompany.fulfilled, (state, action) => {
        state.items = state.items.filter((company) => company.id !== action.payload);
      })
      .addMatcher(
        (action) => action.type.startsWith('companies/') && action.type.endsWith('/rejected'),
        (state, action) => {
          state.error = action.error.message;
        },
      );
  },
});

const employeesSlice = createSlice({
  name: 'employees',
  initialState: {
    items: [],
    loading: false,
    error: '',
  },
  reducers: {
    clearEmployeeError: (state) => {
      state.error = '';
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchEmployees.pending, (state) => {
        state.loading = true;
        state.error = '';
      })
      .addCase(fetchEmployees.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload;
      })
      .addCase(fetchEmployees.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message;
      })
      .addCase(createEmployee.fulfilled, (state, action) => {
        state.items.push(action.payload);
      })
      .addCase(updateEmployee.fulfilled, (state, action) => {
        state.items = state.items.map((employee) => (
          employee.id === action.payload.id ? action.payload : employee
        ));
      })
      .addCase(deleteEmployee.fulfilled, (state, action) => {
        state.items = state.items.filter((employee) => employee.id !== action.payload);
      })
      .addCase(deleteCompany.fulfilled, (state, action) => {
        state.items = state.items.filter((employee) => employee.companyId !== action.payload);
      })
      .addMatcher(
        (action) => action.type.startsWith('employees/') && action.type.endsWith('/rejected'),
        (state, action) => {
          state.error = action.error.message;
        },
      );
  },
});

const store = configureStore({
  reducer: {
    companies: companiesSlice.reducer,
    employees: employeesSlice.reducer,
  },
});

const emptyCompanyForm = { name: '' };
const emptyEmployeeForm = { name: '', age: '', companyId: '' };

function App() {
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

  const totalAge = employees.reduce((sum, employee) => sum + Number(employee.age || 0), 0);
  const averageAge = employees.length === 0 ? 0 : Math.round(totalAge / employees.length);

  const submitCompany = async (event) => {
    event.preventDefault();
    const name = companyForm.name.trim();
    if (!name) {
      return;
    }

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
    const age = Number(employeeForm.age);
    const companyId = employeeForm.companyId;
    if (!name || !age || !companyId) {
      return;
    }

    if (employeeEditId) {
      await dispatch(updateEmployee({ id: employeeEditId, name, age, companyId }));
      setEmployeeEditId('');
    } else {
      await dispatch(createEmployee({ name, age, companyId }));
    }
    setEmployeeForm({ ...emptyEmployeeForm, companyId: companies[0]?.id || '' });
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
      setEmployeeForm((current) => ({ ...current, companyId: companies.find((company) => company.id !== id)?.id || '' }));
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
        <div className="panel">
          <PanelHeader
            title="Компании"
            loading={companiesLoading}
            actionLabel={companyEditId ? 'Изменить' : 'Добавить'}
          />
          {companyError && (
            <Alert message={companyError} onClose={() => dispatch(companiesSlice.actions.clearCompanyError())} />
          )}
          <form className="form-row" onSubmit={submitCompany}>
            <input
              value={companyForm.name}
              onChange={(event) => setCompanyForm({ name: event.target.value })}
              placeholder="Название компании"
              aria-label="Название компании"
            />
            <button type="submit">{companyEditId ? 'Сохранить' : 'Создать'}</button>
            {companyEditId && (
              <button
                type="button"
                className="button-muted"
                onClick={() => {
                  setCompanyEditId('');
                  setCompanyForm(emptyCompanyForm);
                }}
              >
                Отмена
              </button>
            )}
          </form>

          <div className="filter-strip">
            <button
              type="button"
              className={activeCompanyId === 'all' ? 'chip active' : 'chip'}
              onClick={() => setActiveCompanyId('all')}
            >
              Все
            </button>
            {companies.map((company) => (
              <button
                type="button"
                key={company.id}
                className={activeCompanyId === company.id ? 'chip active' : 'chip'}
                onClick={() => setActiveCompanyId(company.id)}
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
                    <button type="button" className="icon-button" title="Редактировать" onClick={() => startCompanyEdit(company)}>
                      Edit
                    </button>
                    <button type="button" className="icon-button danger" title="Удалить" onClick={() => removeCompany(company.id)}>
                      Del
                    </button>
                  </div>
                </article>
              );
            })}
          </div>
        </div>

        <div className="panel wide">
          <PanelHeader
            title="Сотрудники"
            loading={employeesLoading}
            actionLabel={employeeEditId ? 'Изменить' : 'Добавить'}
          />
          {employeeError && (
            <Alert message={employeeError} onClose={() => dispatch(employeesSlice.actions.clearEmployeeError())} />
          )}
          <form className="employee-form" onSubmit={submitEmployee}>
            <input
              value={employeeForm.name}
              onChange={(event) => setEmployeeForm({ ...employeeForm, name: event.target.value })}
              placeholder="Имя сотрудника"
              aria-label="Имя сотрудника"
            />
            <input
              value={employeeForm.age}
              onChange={(event) => setEmployeeForm({ ...employeeForm, age: event.target.value })}
              type="number"
              min="1"
              max="120"
              placeholder="Возраст"
              aria-label="Возраст"
            />
            <select
              value={employeeForm.companyId}
              onChange={(event) => setEmployeeForm({ ...employeeForm, companyId: event.target.value })}
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
              <button
                type="button"
                className="button-muted"
                onClick={() => {
                  setEmployeeEditId('');
                  setEmployeeForm({ ...emptyEmployeeForm, companyId: companies[0]?.id || '' });
                }}
              >
                Отмена
              </button>
            )}
          </form>

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
                      <button type="button" className="icon-button" title="Редактировать" onClick={() => startEmployeeEdit(employee)}>
                        Edit
                      </button>
                      <button type="button" className="icon-button danger" title="Удалить" onClick={() => dispatch(deleteEmployee(employee.id))}>
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
      </section>
    </main>
  );
}

function Metric({ label, value }) {
  return (
    <div className="metric">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function PanelHeader({ title, loading, actionLabel }) {
  return (
    <div className="panel-header">
      <div>
        <h2>{title}</h2>
        <p>{loading ? 'Загрузка данных' : actionLabel}</p>
      </div>
    </div>
  );
}

function Alert({ message, onClose }) {
  return (
    <div className="alert">
      <span>{message}</span>
      <button type="button" onClick={onClose}>Закрыть</button>
    </div>
  );
}

function EmptyState({ title }) {
  return <div className="empty-state">{title}</div>;
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </React.StrictMode>,
);
