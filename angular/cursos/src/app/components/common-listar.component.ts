import { Component, OnInit, ViewChild } from '@angular/core';
import swal from 'sweetalert2'
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { Generic } from '../models/generic';
import { CommonService } from '../services/common.service';

export abstract class CommonListarComponent<E extends Generic, S extends CommonService<E>> implements OnInit {

  titulo: string;
  lista: E[];
  protected nombreModel: string;

  totalRegistros= 0;
  paginaActual=0;
  totalPorPagina=4;
  pageSizeOptions:number[]=[3, 5, 10, 25, 100]

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(protected service: S) { }

  ngOnInit(){

    this.calcularRango();

  }

  public paginar(event: PageEvent):void{
    this.paginaActual=event.pageIndex;
    this.totalPorPagina = event.pageSize;
    this.calcularRango();
  }

  private calcularRango(){
    this.service.listarPaginas(this.paginaActual.toString(), this.totalPorPagina.toString())
    .subscribe(p => {
      this.lista = p.content as E[];
      this.totalRegistros = p.totalElements as number;
      this.paginator._intl.itemsPerPageLabel='Registros por pagina';
    });
  }

  public eliminar(e: E): void{

    swal.fire({
      title: 'Atencion',
      text: `¿Seguro que desea eliminar a ${e.nombre} ?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Si, Borralo!'
    }).then((result) => {
      if (result.value) {
          this.service.eliminar(e.id).subscribe(() =>{
            this.calcularRango();
            swal.fire('Eliminado:', `${this.nombreModel} ${e.nombre} eliminado con exito`, 'success');
          });
        }
      });
    }
  }
