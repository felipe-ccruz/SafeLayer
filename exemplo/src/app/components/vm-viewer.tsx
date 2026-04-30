import { X, Power } from "lucide-react";
import type { VM } from "./vm-card";

interface VMViewerProps {
  vm: VM | null;
  onClose: () => void;
  onToggle: (id: string) => void;
}

export function VMViewer({ vm, onClose, onToggle }: VMViewerProps) {
  if (!vm) return null;
  const running = vm.status === "running";
  const osLabel = vm.os === "windows10" ? "Windows 10" : "Windows 11";
  const wallpaper = vm.os === "windows10"
    ? "from-sky-500 via-blue-600 to-indigo-700"
    : "from-indigo-500 via-purple-600 to-pink-600";

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex flex-col">
      <div className="flex items-center justify-between px-5 py-3 bg-slate-900 border-b border-slate-800">
        <div className="flex items-center gap-3">
          <div className={`w-2 h-2 rounded-full ${running ? "bg-emerald-400" : "bg-slate-500"}`} />
          <span className="text-white">{vm.name}</span>
          <span className="text-slate-400 text-sm">· {osLabel}</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => onToggle(vm.id)}
            className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm transition-colors ${
              running ? "bg-red-500/20 hover:bg-red-500/30 text-red-300" : "bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300"
            }`}
          >
            <Power className="w-4 h-4" />
            {running ? "Parar" : "Iniciar"}
          </button>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>
      </div>

      <div className="flex-1 p-6 flex items-center justify-center">
        <div className={`w-full max-w-5xl aspect-video rounded-2xl overflow-hidden shadow-2xl relative ${
          running ? `bg-gradient-to-br ${wallpaper}` : "bg-slate-900"
        }`}>
          {running ? (
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="text-white/90 text-center">
                <div className="text-5xl mb-2">◉</div>
                <div>{osLabel}</div>
                <div className="text-white/60 text-sm mt-1">máquina virtual em execução</div>
              </div>
            </div>
          ) : (
            <div className="absolute inset-0 flex items-center justify-center text-slate-500 text-sm">
              máquina desligada
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
