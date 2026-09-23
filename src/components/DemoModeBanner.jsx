import React, { useState } from 'react';
import {
  Sparkles,
  Info,
  ChevronDown,
  ChevronUp,
  Activity,
  Cpu,
  ShieldCheck,
  Database,
  Play,
  X
} from 'lucide-react';

export default function DemoModeBanner({ onStartDemo, onOpenAuditModal }) {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div
      style={{
        backgroundColor: '#FFF8E7',
        border: '1px solid #FFE082',
        borderRadius: '16px',
        padding: '10px 14px',
        boxShadow: '0 2px 8px rgba(183, 129, 3, 0.08)',
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        position: 'relative',
        transition: 'all 0.25s ease'
      }}
    >
      {/* Top row: Badges & Summary */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span
            style={{
              backgroundColor: '#E65100',
              color: '#FFFFFF',
              fontSize: '10px',
              fontWeight: '900',
              letterSpacing: '0.6px',
              padding: '3px 8px',
              borderRadius: '8px',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '4px',
              boxShadow: '0 2px 4px rgba(230, 81, 0, 0.25)'
            }}
          >
            <span
              style={{
                width: '6px',
                height: '6px',
                borderRadius: '50%',
                backgroundColor: '#FFF',
                display: 'inline-block',
                animation: 'pulseIdle 1.5s infinite'
              }}
            />
            DEMO MODE
          </span>
          <span style={{ fontSize: '11px', fontWeight: '700', color: '#6A4D00' }}>
            Simulated Wearable • Live AI Active
          </span>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          {onStartDemo && (
            <button
              onClick={onStartDemo}
              style={{
                backgroundColor: '#E65100',
                color: '#FFF',
                border: 'none',
                borderRadius: '8px',
                padding: '4px 8px',
                fontSize: '10px',
                fontWeight: '800',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '3px'
              }}
              title="Run 90-second deterministic flow"
            >
              <Play size={10} fill="#FFF" /> 90s Demo
            </button>
          )}

          <button
            onClick={() => setIsExpanded(!isExpanded)}
            style={{
              background: '#FFE082',
              border: 'none',
              borderRadius: '8px',
              padding: '4px 8px',
              fontSize: '10px',
              fontWeight: '700',
              color: '#6A4D00',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '2px'
            }}
            aria-label="Toggle real versus mocked info"
          >
            {isExpanded ? 'Hide Info' : "What's Real?"}
            {isExpanded ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
          </button>
        </div>
      </div>

      {/* Expanded breakdown drawer */}
      {isExpanded && (
        <div
          style={{
            borderTop: '1px dashed #FFE082',
            paddingTop: '8px',
            display: 'flex',
            flexDirection: 'column',
            gap: '8px',
            fontSize: '11px'
          }}
        >
          {/* Real components */}
          <div style={{ backgroundColor: '#FFFFFF', borderRadius: '10px', padding: '8px 10px', border: '1px solid #E8F5E9' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#2E7D32', fontWeight: '800', marginBottom: '4px' }}>
              <ShieldCheck size={14} /> Real Production Functionality:
            </div>
            <ul style={{ margin: 0, paddingLeft: '18px', color: '#2E7D32', lineHeight: '16px' }}>
              <li><strong>Gemini 3.6 Flash AI:</strong> Live conversational API responses via environment key</li>
              <li><strong>Reality Engine:</strong> Dynamic stressor identification & 3-step micro-task decomposition</li>
              <li><strong>Zero-Latency Crisis Flow:</strong> Immediate intercept for crisis terms & 24/7 helplines</li>
              <li><strong>Local Storage Persistence:</strong> Tasks, daily check-ins, and journal entries saved locally</li>
              <li><strong>Smart Nudge Manager:</strong> Quiet hours (10 PM–7 AM) & duplicate prevention</li>
            </ul>
          </div>

          {/* Mocked components */}
          <div style={{ backgroundColor: '#FFFFFF', borderRadius: '10px', padding: '8px 10px', border: '1px solid #FFE0B2' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#D84315', fontWeight: '800', marginBottom: '4px' }}>
              <Activity size={14} /> Mocked / Simulated Data:
            </div>
            <ul style={{ margin: 0, paddingLeft: '18px', color: '#BF360C', lineHeight: '16px' }}>
              <li><strong>Wearable Telemetry:</strong> 74 bpm, 38/100 stress score, 3,420 steps, and sleep score (from <code>MockWearableAdapter</code>)</li>
              <li><strong>BLE Connection:</strong> Software simulation toggle (Real hardware connects via Zepp OS GATT profiles)</li>
            </ul>
          </div>

          {onOpenAuditModal && (
            <button
              onClick={onOpenAuditModal}
              style={{
                background: 'none',
                border: 'none',
                color: '#1C3F30',
                fontSize: '10px',
                fontWeight: '800',
                textDecoration: 'underline',
                cursor: 'pointer',
                textAlign: 'right'
              }}
            >
              View Full Architecture Audit Details ➔
            </button>
          )}
        </div>
      )}
    </div>
  );
}
