package com.ingenieriajhr.visualcontroll.accessibility.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ingenieriajhr.visualcontroll.R;

import java.util.ArrayList;

public class SimpleCustomAdapter extends RecyclerView.Adapter<SimpleCustomAdapter.ViewHolder>{

    // Interfaz para que MainActivity u otra actividad implemente el listener y pueda capturar el evento fuera del adaptador
    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    // Colección de objetos que se va a mostrar
    private ArrayList<ModelCiberPaz> dataSet;
    // Variable para capturar los clics fuera del adaptador
    private ItemClickListener clicListener;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Referencias a los elementos visuales
        private TextView txtTittleCiberPaz, txtContentCiberPaz;
        private ImageView imgRecycler;

        private CardView containerLayoutRecycler;

        public ViewHolder(View view) {
            super(view);
            // Asignamos el listener de clic al item
            view.setOnClickListener(this);
            txtTittleCiberPaz = view.findViewById(R.id.txtTitleRecycler);
            txtContentCiberPaz = view.findViewById(R.id.txtContentRecycler);
            imgRecycler = view.findViewById(R.id.imgRecycler);
            containerLayoutRecycler = view.findViewById(R.id.containerLayoutRecycler);
        }

        // Getter para txtTittleCiberPaz
        public TextView getTxtTittleCiberPaz() {
            return txtTittleCiberPaz;
        }

        // Método que maneja el clic en el item y lo propaga hacia fuera
        @Override
        public void onClick(View view) {
            if (clicListener != null) {
                clicListener.onClick(view, getAdapterPosition());
            }
        }
    }

    // Constructor donde pasamos el dataset que se mostrará
    public SimpleCustomAdapter(Context applicationContext, ArrayList<ModelCiberPaz> dataSet) {
        this.dataSet = dataSet;
        this.context = applicationContext;
    }

    // Método que permite configurar el listener desde la actividad
    public void setOnClickListener(ItemClickListener clicListener) {
        this.clicListener = clicListener;
    }

    // Este método se llama una sola vez cuando se crea el ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Inflamos la vista de cada item desde el layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_layout_ciberpaz, viewGroup, false);
        return new ViewHolder(view);
    }

    // Este método se llama cuando un item se debe mostrar o actualizar en la vista
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Se obtienen los datos y se asignan a los elementos visuales del ViewHolder
        ModelCiberPaz model = dataSet.get(position);
        viewHolder.getTxtTittleCiberPaz().setText(model.getTitulo()); // Utilizamos el getter para obtener el título
        viewHolder.txtContentCiberPaz.setText(model.getDescripcion()); // Asumiendo que hay un campo 'descripcion'
        viewHolder.containerLayoutRecycler.setBackgroundTintList(ContextCompat.getColorStateList(context,model.getColorFondo()));
        viewHolder.imgRecycler.setImageResource(model.getImagen());
    }

    // Devuelve el número de items del dataset
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
