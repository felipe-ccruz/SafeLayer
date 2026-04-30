import { Play, Square, Trash2, Monitor } from "lucide-react";

export type VMStatus = "stopped" | "running";
export type VMOS = "windows10" | "windows11";

export interface VM {
  id: string;
  name: string;
  os: VMOS;
  status: VMStatus;
  createdAt: number;
}

interface VMCardProps {
  vm: VM;
  onToggle: (id: string) => void;
  onDelete: (id: string) => void;
  onOpen: (id: string) => void;
}

const osLabels: Record<VMOS, string> = {
  windows10: "Windows 10",
  windows11: "Windows 11",
};

const osGradients: Record<VMOS, string> = {
  windows10: "from-sky-400 to-blue-600",
  windows11: "from-indigo-400 to-purple-600",
};

export function VMCard({ vm, onToggle, onDelete, onOpen }: VMCardProps) {
  const running = vm.status === "running";

  return (
    <div className="group bg-white rounded-2xl border border-slate-200 hover:border-slate-300 hover:shadow-lg transition-all overflow-hidden">
      <button
        onClick={() => onOpen(vm.id)}
        className={`w-full aspect-video bg-gradient-to-br ${osGradients[vm.os]} flex items-center justify-center relative`}
      >
        <Monitor className="w-16 h-16 text-white/90" strokeWidth={1.5} />
        <div className="absolute top-3 right-3 flex items-center gap-1.5 bg-black/20 backdrop-blur-sm px-2.5 py-1 rounded-full">
          <span className={`w-1.5 h-1.5 rounded-full ${running ? "bg-emerald-400 animate-pulse" : "bg-slate-300"}`} />
          <span className="text-white text-xs">{running ? "Ligada" : "Desligada"}</span>
        </div>
      </button>

      <div className="p-4">
        <div className="mb-3">
          <h3 className="text-slate-900 truncate">{vm.name}</h3>
          <p className="text-sm text-slate-500">{osLabels[vm.os]}</p>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => onToggle(vm.id)}
            className={`flex-1 flex items-center justify-center gap-2 py-2 rounded-xl transition-colors ${
              running
                ? "bg-slate-100 hover:bg-slate-200 text-slate-700"
                : "bg-slate-900 hover:bg-slate-800 text-white"
            }`}
          >
            {running ? <Square className="w-4 h-4" /> : <Play className="w-4 h-4" />}
            <span className="text-sm">{running ? "Parar" : "Iniciar"}</span>
          </button>
          <button
            onClick={() => onDelete(vm.id)}
            className="p-2 rounded-xl text-slate-400 hover:text-red-500 hover:bg-red-50 transition-colors"
            aria-label="Excluir"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
