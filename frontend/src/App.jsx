import { useEffect, useMemo, useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? ''
const CELLAR_ENDPOINT = `${API_BASE_URL}/api/v1/cellar/get`

async function fetchCellar() {
  const response = await fetch(CELLAR_ENDPOINT, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ cellarName: 'My Cellar' }),
  })

  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || `Request failed with status ${response.status}`)
  }

  return response.json()
}

function JsonBox({ title, payload }) {
  // eslint-disable-next-line react/prop-types
  const pretty = useMemo(() => JSON.stringify(payload, null, 2), [payload])

  return (
    <div className="json-box">
      <h3>{title}</h3>
      <pre>{pretty}</pre>
    </div>
  )
}

function App() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [cellar, setCellar] = useState(null)

  useEffect(() => {
    let canceled = false
    setLoading(true)
    fetchCellar()
      .then((payload) => {
        if (!canceled) {
          setCellar(payload)
          setError(null)
        }
      })
      .catch((err) => {
        if (!canceled) {
          setError(err.message)
          setCellar(null)
        }
      })
      .finally(() => {
        if (!canceled) {
          setLoading(false)
        }
      })

    return () => {
      canceled = true
    }
  }, [])

  return (
    <div className="app-shell">
      <h1>Cellar</h1>
      {loading && <p>Loading cellar data…</p>}
      {error && <p className="error">{error}</p>}
      {cellar && (
        <>
          {/*<JsonBox title="Cellar" payload={cellar} />*/}
          <div className="cask-grid">
            {cellar.casks?.map((cask) => (
              <JsonBox key={cask.name} title={cask.name} payload={cask} />
            ))}
          </div>
        </>
      )}
    </div>
  )
}

export default App
