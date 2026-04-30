import { useState } from "react";
import { X, Check } from "lucide-react";
import type { VMOS } from "./vm-card";

interface CreateVMModalProps {
  open: boolean;
  onClose: () => void;
  onCreate: (name: string, os: VMOS) => void;
}

export function CreateVMModal({ open, onClose, onCreate }: CreateVMModalProps) {
  const [name, setName] = useState("");
  const [os, setOs] = useState<VMOS>("windows11");

  if (!open) return null;

  const handleCreate = () => {
    const trimmed = name.trim();
    if (!trimmed) return;
    onCreate(trimmed, os);
    setName("");
    setOs("windows11");
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-sm" onClick={onClose}>
      <div
        className="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between px-6 pt-6 pb-2">
          <h2 className="text-slate-900">Nova máquina virtual</h2>
          <button
            onClick={onClose}
            className="p-1.5 rounded-full text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="px-6 py-4 space-y-5">
          <div>
            <label className="block text-sm text-slate-600 mb-2">Nome</label>
            <input
              autoFocus
              value={name}
              onChange={(e) => setName(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleCreate()}
              placeholder="Minha VM"
              className="w-full px-4 py-3 rounded-xl border border-slate-200 focus:border-slate-900 focus:ring-0 outline-none transition-colors"
            />
          </div>

          <div>
            <label className="block text-sm text-slate-600 mb-2">Sistema</label>
            <div className="grid grid-cols-2 gap-3">
              {([
                { id: "windows10", label: "Windows 10", grad: "from-sky-400 to-blue-600" },
                { id: "windows11", label: "Windows 11", grad: "from-indigo-400 to-purple-600" },
              ] as const).map((opt) => {
                const selected = os === opt.id;
                return (
                  <button
                    key={opt.id}
                    onClick={() => setOs(opt.id)}
                    className={`relative rounded-2xl p-4 border-2 transition-all text-left ${
                      selected ? "border-slate-900" : "border-slate-200 hover:border-slate-300"
                    }`}
                  >
                    <div className={`w-full aspect-video rounded-lg bg-gradient-to-br ${opt.grad} mb-3`} />
                    <div className="text-sm text-slate-900">{opt.label}</div>
                    {selected && (
                      <div className="absolute top-2 right-2 w-5 h-5 rounded-full bg-slate-900 flex items-center justify-center">
                        <Check className="w-3 h-3 text-white" strokeWidth={3} />
                      </div>
                    )}
                  </button>
                );
              })}
            </div>
          </div>
        </div>

        <div className="px-6 pb-6 pt-2 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 py-3 rounded-xl text-slate-700 hover:bg-slate-100 transition-colors"
          >
            Cancelar
          </button>
          <button
            onClick={handleCreate}
            disabled={!name.trim()}
            className="flex-1 py-3 rounded-xl bg-slate-900 text-white hover:bg-slate-800 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          >
            Criar
          </button>
        </div>
      </div>
    </div>
  );
}
